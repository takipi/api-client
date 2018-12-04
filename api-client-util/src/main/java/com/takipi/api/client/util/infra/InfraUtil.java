package com.takipi.api.client.util.infra;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.event.Location;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.data.view.ViewFilters;
import com.takipi.api.client.data.view.ViewInfo;
import com.takipi.api.client.request.event.EventModifyLabelsRequest;
import com.takipi.api.client.request.event.EventRequest;
import com.takipi.api.client.request.label.CreateLabelRequest;
import com.takipi.api.client.request.view.CreateViewRequest;
import com.takipi.api.client.request.view.ViewsRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.view.CreateViewResult;
import com.takipi.api.client.result.view.ViewsResult;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class InfraUtil {
	private static final String INFRA_SUFFIX = ".infra";

	public static void categorizeEvent(String eventId, String serviceId, String categoryId, Categories categories,
			Set<String> existingLabels, ApiClient apiClient, boolean applyLabels) {
		EventRequest metadataRequest = EventRequest.newBuilder().setEventId(eventId).setServiceId(serviceId).build();

		Response<EventResult> metadataResult = apiClient.get(metadataRequest);

		if (metadataResult.isBadResponse()) {
			throw new IllegalStateException("Can't apply infrastructure routing to event " + eventId);
		}

		categorizeEvent(metadataResult.data, serviceId, categoryId, categories, existingLabels, apiClient, applyLabels);
	}

	public static Pair<Collection<String>, Collection<String>> categorizeEvent(EventResult event, String serviceId,
			String categoryId, Categories categories, Set<String> existingLabels, ApiClient apiClient,
			boolean applyLabels) {
		if ((event == null) || (event.error_origin == null)) {
			return Pair.of(Collections.emptySet(), Collections.emptySet());
		}

		Set<String> labelsToRemove = Sets.newHashSet();

		if (!CollectionUtil.safeIsEmpty(event.labels)) {
			for (String currentLabel : event.labels) {
				if (currentLabel.endsWith(INFRA_SUFFIX)) {
					labelsToRemove.add(currentLabel);
				}
			}
		}

		Set<String> labelsToAdd = Sets.newHashSet();

		Location errorOrigin = event.error_origin;

		Set<String> locationLabels = categories.getCategories(errorOrigin.class_name);

		if (!CollectionUtil.safeIsEmpty(locationLabels)) {
			for (String locationLabel : locationLabels) {
				String infraLabelName = toInternalInfraLabelName(locationLabel);

				labelsToRemove.remove(infraLabelName);

				if (CollectionUtil.safeContains(event.labels, infraLabelName)) {
					continue;
				}

				labelsToAdd.add(infraLabelName);

				if (existingLabels.add(locationLabel)) {
					validateInfraView(locationLabel, categoryId, serviceId, apiClient);
				}
			}
		}

		if (!applyLabels) {
			return Pair.of(labelsToAdd, labelsToRemove);
		}

		if ((!labelsToAdd.isEmpty()) || (!labelsToRemove.isEmpty())) {
			EventModifyLabelsRequest labelsRequest = EventModifyLabelsRequest.newBuilder().setServiceId(serviceId)
					.setEventId(event.id).addLabels(labelsToAdd).removeLabels(labelsToRemove).build();

			Response<EmptyResult> addResult = apiClient.post(labelsRequest);

			if (addResult.isBadResponse()) {
				throw new IllegalStateException("Can't apply labels to event " + event.id);
			}
		}

		return Pair.of(labelsToAdd, labelsToRemove);
	}

	private static void validateInfraView(String locationLabel, String categoryId, String serviceId,
			ApiClient apiClient) {
		boolean labelExisted = createInfraLabel(locationLabel, serviceId, apiClient);

		if (labelExisted) {
			return;
		}

		String viewId = createInfraView(locationLabel, serviceId, apiClient);
		ViewUtil.addViewToCategory(categoryId, viewId, serviceId, apiClient);
	}

	// Returns true if the label already existed.
	//
	private static boolean createInfraLabel(String labelName, String serviceId, ApiClient apiClient) {
		String infraLabelName = toInternalInfraLabelName(labelName);

		CreateLabelRequest createLabelRequest = CreateLabelRequest.newBuilder().setServiceId(serviceId)
				.setName(infraLabelName).build();

		Response<EmptyResult> createResponse = apiClient.post(createLabelRequest);

		if ((createResponse.isBadResponse()) && (createResponse.responseCode != HttpURLConnection.HTTP_CONFLICT)) {
			throw new IllegalStateException("Can't create label " + infraLabelName);
		}

		return (createResponse.responseCode == HttpURLConnection.HTTP_CONFLICT);
	}

	private static String createInfraView(String labelName, String serviceId, ApiClient apiClient) {
		ViewFilters viewFilters = new ViewFilters();
		viewFilters.labels = Collections.singletonList(toInternalInfraLabelName(labelName));

		ViewInfo viewInfo = new ViewInfo();
		viewInfo.name = labelName;
		viewInfo.shared = true;
		viewInfo.filters = viewFilters;

		CreateViewRequest createViewRequest = CreateViewRequest.newBuilder().setServiceId(serviceId)
				.setViewInfo(viewInfo).build();

		Response<CreateViewResult> createViewResponse = apiClient.post(createViewRequest);

		if (createViewResponse.isOK()) {
			CreateViewResult createViewResult = createViewResponse.data;

			if ((createViewResult == null) || (Strings.isNullOrEmpty(createViewResult.view_id))) {
				throw new IllegalStateException("Failed creating view - " + labelName);
			}

			return createViewResult.view_id;
		}

		if (createViewResponse.responseCode != HttpURLConnection.HTTP_CONFLICT) {
			throw new IllegalStateException("Failed creating view - " + labelName);
		}

		ViewsRequest getViewsRequest = ViewsRequest.newBuilder().setServiceId(serviceId).setViewName(labelName).build();

		Response<ViewsResult> getViewsResponse = apiClient.get(getViewsRequest);

		if ((getViewsResponse.isBadResponse()) || (getViewsResponse.data == null)
				|| (CollectionUtil.safeIsEmpty(getViewsResponse.data.views))) {
			throw new IllegalStateException("Failed getting view - " + labelName);
		}

		SummarizedView view = getViewsResponse.data.views.get(0);

		if ((!labelName.equalsIgnoreCase(view.name)) || (Strings.isNullOrEmpty(view.id))) {
			throw new IllegalStateException("Failed getting view - " + labelName);
		}

		return view.id;
	}

	private static String toInternalInfraLabelName(String labelName) {
		return labelName + INFRA_SUFFIX;
	}
}
