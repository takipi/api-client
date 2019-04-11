package com.takipi.api.client.util.infra;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.event.Location;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.data.view.ViewFilters;
import com.takipi.api.client.data.view.ViewInfo;
import com.takipi.api.client.request.event.EventModifyLabelsRequest;
import com.takipi.api.client.request.event.EventRequest;
import com.takipi.api.client.request.label.CreateLabelRequest;
import com.takipi.api.client.request.reliability.UpdateReliabilitySettingsRequest;
import com.takipi.api.client.request.view.CreateViewRequest;
import com.takipi.api.client.request.view.ViewsRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.view.CreateViewResult;
import com.takipi.api.client.result.view.ViewsResult;
import com.takipi.api.client.util.category.CategoryUtil;
import com.takipi.api.client.util.infra.Categories.Category;
import com.takipi.api.client.util.infra.Categories.CategoryType;
import com.takipi.api.client.util.settings.ServiceSettingsData;
import com.takipi.api.client.util.settings.SettingsUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class InfraUtil {

	private static final String TIER_LABEL_SEPERATOR = ".";

	public static void categorizeEvent(String eventId, String serviceId, 
			Map<CategoryType, String> categoryIds, Categories categories,
			Set<String> existingLabels, ApiClient apiClient, boolean applyLabels) {
				
		EventRequest metadataRequest = EventRequest.newBuilder().setEventId(eventId).setServiceId(serviceId)
			.setIncludeStacktrace(true).build();

		Response<EventResult> metadataResult = apiClient.get(metadataRequest);

		if (metadataResult.isBadResponse()) {
			throw new IllegalStateException("Can't apply infrastructure routing to event " + eventId);
		}

		categorizeEvent(metadataResult.data, serviceId, categoryIds, categories, existingLabels, apiClient, applyLabels);
	}
	
	private static void updateEventLabels(EventResult event,
		Set<String> tierLabels, Set<String> existingLabels,
		Set<String> labelsToRemove, Set<String> labelsToAdd,
		CategoryType type, String serviceId, String categoryId, ApiClient apiClient) {
			
		if (!CollectionUtil.safeIsEmpty(tierLabels)) {
			
			for (String tierLabel : tierLabels) {
				String labelName = toTierLabelName(tierLabel, type);

				labelsToRemove.remove(labelName);

				if (CollectionUtil.safeContains(event.labels, labelName)) {
					continue;
				}

				labelsToAdd.add(labelName);

				if (existingLabels.add(tierLabel)) {
					validateTierView(tierLabel, type, 
						categoryId, serviceId, apiClient);
				}
			}
		}
	}

	public static Pair<Collection<String>, Collection<String>> categorizeEvent(EventResult event, 
		String serviceId, Map<CategoryType, String> categoryIds, Categories categories, Set<String> existingLabels, 
		ApiClient apiClient, boolean applyLabels) {
		
		if ((event == null) || (event.error_origin == null)) {
			return Pair.of(Collections.emptySet(), Collections.emptySet());
		}

		Set<String> labelsToRemove = getEventTiers(event);
		Set<String> labelsToAdd = Sets.newHashSet();

		Location errorOrigin = event.error_origin;

		Set<String> infraLabels = categories.getCategories(errorOrigin.class_name, CategoryType.infra);
		Set<String> appLabels = Sets.newHashSet();

		if (!CollectionUtil.safeIsEmpty(event.stack_frames)) {
			for (Location location : event.stack_frames) {
				Collection<String> frameMatches = categories.getCategories(location.class_name, CategoryType.app);
				appLabels.addAll(frameMatches);
			}
		}
		
		updateEventLabels(event, appLabels, existingLabels, 
			labelsToRemove, labelsToAdd, CategoryType.app, 
			serviceId, categoryIds.get(CategoryType.app),
			apiClient);
		
		updateEventLabels(event, infraLabels, existingLabels, 
			labelsToRemove, labelsToAdd, CategoryType.infra, 
			serviceId, categoryIds.get(CategoryType.infra), apiClient);

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

	private static void validateTierView(String locationLabel, 
		CategoryType type, String categoryId, String serviceId, ApiClient apiClient) {
		
		boolean labelExisted = createTierLabel(locationLabel, type, serviceId, apiClient);

		if (labelExisted) {
			return;
		}

		String viewId = createTierView(locationLabel, type, serviceId, apiClient);
		CategoryUtil.addViewToCategory(categoryId, viewId, serviceId, apiClient);
	}

	// Returns true if the label already existed.
	//
	private static boolean createTierLabel(String labelName, CategoryType type,
		String serviceId, ApiClient apiClient) {
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

	private static Set<String> getEventTiers(EventResult event) {
		
		if (CollectionUtil.safeIsEmpty(event.labels)) {
			return Collections.emptySet();
		}
		
		Set<String> result = Sets.newHashSet();
		
		for (String label : event.labels) {
			
			for (CategoryType type : CategoryType.values()) {
				if (label.endsWith(getTierLabelPostfix(type))) {
					result.add(label);
				}
			}
		}
		
		return result;
	}
	
	private static String getTierLabelPostfix(CategoryType type) {
		return TIER_LABEL_SEPERATOR + type.toString().toLowerCase();
	}
		
	public static String getTierNameFromLabel(String tierName, CategoryType type) {
		
		String postfix = getTierLabelPostfix(type);
		
		if ((tierName == null) || (tierName.isEmpty()) 
		|| (!tierName.endsWith(postfix))) {
			return null;
		}
		
		String result = tierName.substring(0, tierName.length() - postfix.length());
		
		return result;
		
	}
	
	public static String toTierLabelName(String tierName, CategoryType type) {
		return tierName + getTierLabelPostfix(type);
	}
		
	public static ServiceSettingsData appendTier(ApiClient apiClient, String serviceId,
		Collection<Category> categories, boolean update) {
		
		ServiceSettingsData result = SettingsUtil.getServiceReliabilitySettings(apiClient, serviceId);
		
		if (result == null) {
			return null;
		}
		
		boolean modified = false;
		
		for (Category category : categories) {
			
			boolean found;
			
			if (!CollectionUtil.safeIsEmpty(result.tiers)) {
				found = result.tiers.contains(category);	
			} else {
				found = false;
			}
			
			if (!found) {
				
				if (result.tiers == null) {
					result.tiers = new ArrayList<Category>();
				}
				
				modified = true;
				result.tiers.add(category);
			}	
		}
		
		if ((update) && (modified)) {
			
			String modifiedJson = new Gson().toJson(result);
			
			UpdateReliabilitySettingsRequest updateReliabilitySettingsRequest = UpdateReliabilitySettingsRequest.newBuilder().
					setServiceId(serviceId).setReliabilitySettingsJson(modifiedJson).build();
			
			Response<EmptyResult> updateReaponse = apiClient.post(updateReliabilitySettingsRequest);
			
			if ((updateReaponse == null) || (updateReaponse.isBadResponse())) {
				throw new IllegalStateException("Could not update reliability settings for " + serviceId + " with " + modifiedJson);
			}
		}
		
		return result;
	}
}
