package com.takipi.api.client.util.infra;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.takipi.api.client.ApiClient;
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
import com.takipi.api.client.util.category.CategoryUtil;
import com.takipi.api.client.util.infra.Categories.CategoryType;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;
import com.takipi.common.util.StringUtil;

public class InfraUtil {

	private static final String TIER_LABEL_SEPERATOR = ".";

	public static void categorizeEvent(String eventId, String serviceId, Map<CategoryType, String> categoryIds,
			Categories categories, Set<String> existingLabels, ApiClient apiClient, boolean applyLabels,
			boolean handleSimilarEvents) {

		boolean includeStacktrace = categoryIds.containsKey(CategoryType.app);

		EventRequest metadataRequest = EventRequest.newBuilder().setEventId(eventId).setServiceId(serviceId)
				.setIncludeStacktrace(includeStacktrace).build();

		Response<EventResult> metadataResult = apiClient.get(metadataRequest);

		if (metadataResult.isBadResponse()) {
			throw new IllegalStateException("Can't apply infrastructure routing to event " + eventId);
		}

		categorizeEvent(metadataResult.data, serviceId, categoryIds, categories, existingLabels, apiClient, applyLabels,
				handleSimilarEvents);
	}

	public static Pair<Collection<String>, Collection<String>> categorizeEvent(EventResult event, String serviceId,
			Map<CategoryType, String> categoryIds, Categories categories, Set<String> existingLabels,
			ApiClient apiClient, boolean applyLabels, boolean handleSimilarEvents) {

		if (event == null) {
			return Pair.of(Collections.emptySet(), Collections.emptySet());
		}

		Set<String> labelsToAdd = new HashSet<>();
		Set<String> labelsToRemove = new HashSet<>();

		for (Map.Entry<CategoryType, String> entry : categoryIds.entrySet()) {
			CategoryType categoryType = entry.getKey();

			Categorizer categorizer = Categorizer.get(categoryType);

			if (categorizer == null) {
				continue;
			}

			String categoryId = entry.getValue();

			categorizer.categorizeEvent(event, categories, labelsToAdd, labelsToRemove, existingLabels, serviceId,
					categoryId, apiClient);
		}

		if (!applyLabels) {
			return Pair.of(labelsToAdd, labelsToRemove);
		}

		if ((!labelsToAdd.isEmpty()) || (!labelsToRemove.isEmpty())) {
			EventModifyLabelsRequest labelsRequest = EventModifyLabelsRequest.newBuilder().setServiceId(serviceId)
					.setEventId(event.id).setHandleSimilarEvents(handleSimilarEvents).addLabels(labelsToAdd)
					.removeLabels(labelsToRemove).build();

			Response<EmptyResult> addResult = apiClient.post(labelsRequest);

			if (addResult.isBadResponse()) {
				throw new IllegalStateException("Can't apply labels to event " + event.id);
			}
		}

		return Pair.of(labelsToAdd, labelsToRemove);
	}

	public static void validateTierView(String locationLabel, CategoryType type, String categoryId, String serviceId,
			ApiClient apiClient) {

		createTierLabel(locationLabel, type, serviceId, apiClient);

		String viewId = createTierView(locationLabel, type, serviceId, apiClient);
		CategoryUtil.addViewToCategory(categoryId, viewId, serviceId, apiClient);
	}

	// Returns true if the label already existed.
	//
	private static boolean createTierLabel(String labelName, CategoryType type, String serviceId, ApiClient apiClient) {
		String tierLabelName = toTierLabelName(labelName, type);

		CreateLabelRequest createLabelRequest = CreateLabelRequest.newBuilder().setServiceId(serviceId)
				.setName(tierLabelName).build();

		Response<EmptyResult> createResponse = apiClient.post(createLabelRequest);

		if ((createResponse.isBadResponse()) && (createResponse.responseCode != HttpURLConnection.HTTP_CONFLICT)) {
			throw new IllegalStateException("Can't create label " + tierLabelName);
		}

		return (createResponse.responseCode == HttpURLConnection.HTTP_CONFLICT);
	}

	private static String createTierView(String labelName, CategoryType type, String serviceId, ApiClient apiClient) {
		ViewFilters viewFilters = new ViewFilters();
		viewFilters.labels = Collections.singletonList(toTierLabelName(labelName, type));

		ViewInfo viewInfo = new ViewInfo();
		viewInfo.name = labelName;
		viewInfo.shared = true;
		viewInfo.filters = viewFilters;
		viewInfo.immutable = true;

		CreateViewRequest createViewRequest = CreateViewRequest.newBuilder().setServiceId(serviceId)
				.setViewInfo(viewInfo).build();

		Response<CreateViewResult> createViewResponse = apiClient.post(createViewRequest);

		if (createViewResponse.isOK()) {
			CreateViewResult createViewResult = createViewResponse.data;

			if ((createViewResult == null) || (StringUtil.isNullOrEmpty(createViewResult.view_id))) {
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

		if ((!labelName.equalsIgnoreCase(view.name)) || (StringUtil.isNullOrEmpty(view.id))) {
			throw new IllegalStateException("Failed getting view - " + labelName);
		}

		return view.id;
	}

	public static String getTierLabelPostfix(CategoryType type) {
		return TIER_LABEL_SEPERATOR + type.toString().toLowerCase();
	}

	public static String getTierNameFromLabel(String tierName, CategoryType type) {
		String postfix = getTierLabelPostfix(type);

		if ((tierName == null) || (tierName.isEmpty()) || (!tierName.endsWith(postfix))) {
			return null;
		}

		String result = tierName.substring(0, tierName.length() - postfix.length());

		return result;

	}

	public static String toTierLabelName(String tierName, CategoryType type) {
		return tierName + getTierLabelPostfix(type);
	}
}
