package com.takipi.api.client.util.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.data.view.ViewFilters;
import com.takipi.api.client.data.view.ViewInfo;
import com.takipi.api.client.request.category.CategoryAddViewRequest;
import com.takipi.api.client.request.event.EventsRequest;
import com.takipi.api.client.request.event.EventsVolumeRequest;
import com.takipi.api.client.request.metrics.GraphRequest;
import com.takipi.api.client.request.view.CreateViewRequest;
import com.takipi.api.client.request.view.DeleteViewRequest;
import com.takipi.api.client.request.view.ViewsRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.client.result.event.EventsVolumeResult;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.result.view.CreateViewResult;
import com.takipi.api.client.result.view.ViewsResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphType;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class ViewUtil {
	private static final DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

	public static void createFilteredView(ApiClient apiClient, String serviceId, ViewInfo viewInfo, String categoryId) {
		createFilteredViews(apiClient, serviceId, Collections.singleton(viewInfo), categoryId);
	}

	public static void createFilteredViews(ApiClient apiClient, String serviceId, Collection<ViewInfo> viewInfos,
			String categoryId) {

		Map<String, SummarizedView> views = getServiceViewsByName(apiClient, serviceId);
		createFilteredViews(apiClient, serviceId, viewInfos, views, categoryId);
	}

	public static void createFilteredViews(ApiClient apiClient, String serviceId, Collection<ViewInfo> viewInfos,
			Map<String, SummarizedView> views, String categoryId) {

		for (ViewInfo viewInfo : viewInfos) {
			createFilteredView(apiClient, serviceId, viewInfo, categoryId, views);
		}
	}

	private static void createFilteredView(ApiClient apiClient, String serviceId, ViewInfo viewInfo, String categoryId,
			Map<String, SummarizedView> views) {

		SummarizedView view = views.get(viewInfo.name);

		if (view != null) {
			System.out.println("view " + viewInfo.name + " found with ID " + view.id);
			return;
		}

		CreateViewRequest createViewRequest = CreateViewRequest.newBuilder().setServiceId(serviceId)
				.setViewInfo(viewInfo).build();

		Response<CreateViewResult> viewResponse = apiClient.post(createViewRequest);

		if ((viewResponse.isBadResponse()) || (viewResponse.data == null)) {
			System.err.println("Cannot create view " + viewInfo.name);
			return;
		}

		System.out.println("Created view " + viewResponse.data.view_id + " for view " + viewInfo.name);

		if (categoryId != null) {
			CategoryAddViewRequest categoryAddViewRequest = CategoryAddViewRequest.newBuilder().setServiceId(serviceId)
					.setViewId(viewResponse.data.view_id).setCategoryId(categoryId).build();

			Response<EmptyResult> categoryAddViewResponse = apiClient.post(categoryAddViewRequest);

			if (categoryAddViewResponse.isBadResponse()) {
				System.out.println("Error adding view " + viewInfo.name + " to category " + categoryId);
			}
		}
	}

	public static void createLabelViewsIfNotExists(ApiClient apiClient, String serviceId,
			Collection<Pair<String, String>> viewsAndLabels, String categoryId) {

		List<ViewInfo> viewInfos = Lists.newArrayList();

		for (Pair<String, String> pair : viewsAndLabels) {

			String viewName = pair.getFirst();
			String labelName = pair.getSecond();

			ViewInfo viewInfo = new ViewInfo();

			viewInfo.name = viewName;
			viewInfo.filters = new ViewFilters();
			viewInfo.filters.labels = Collections.singletonList(labelName);
			viewInfo.shared = true;

			viewInfos.add(viewInfo);
		}

		createFilteredViews(apiClient, serviceId, viewInfos, categoryId);
	}

	public static Map<String, SummarizedView> getServiceViewsByName(ApiClient apiClient, String serviceId) {
		ViewsRequest viewsRequest = ViewsRequest.newBuilder().setServiceId(serviceId).build();

		Response<ViewsResult> viewsResponse = apiClient.get(viewsRequest);

		if ((viewsResponse.isBadResponse()) || (viewsResponse.data == null) || (viewsResponse.data.views == null)) {
			System.err.println("Can't list views");
			return Collections.emptyMap();
		}

		Map<String, SummarizedView> result = Maps.newHashMap();

		for (SummarizedView view : viewsResponse.data.views) {
			result.put(view.name, view);
		}

		return result;
	}

	public static SummarizedView getServiceViewByName(ApiClient apiClient, String serviceId, String viewName) {
		ViewsRequest viewsRequest = ViewsRequest.newBuilder().setServiceId(serviceId).setViewName(viewName).build();

		Response<ViewsResult> viewsResponse = apiClient.get(viewsRequest);

		if ((viewsResponse.isBadResponse()) || (viewsResponse.data == null) || (viewsResponse.data.views == null)
				|| (viewsResponse.data.views.size() == 0)) {
			return null;
		}

		SummarizedView result = viewsResponse.data.views.get(0);

		return result;
	}

	public static EventsVolumeResult getEventsVolume(ApiClient apiClient, String serviceId, String viewId,
			DateTime from, DateTime to) {
		EventsVolumeRequest eventsVolumeRequest = EventsVolumeRequest.newBuilder().setServiceId(serviceId)
				.setViewId(viewId).setFrom(from.toString(fmt)).setTo(to.toString(fmt)).setVolumeType(VolumeType.all)
				.build();

		Response<EventsVolumeResult> eventsVolumeResponse = apiClient.get(eventsVolumeRequest);

		if (eventsVolumeResponse.isBadResponse()) {
			return null;
		}

		EventsVolumeResult eventsVolumeResult = eventsVolumeResponse.data;

		if (eventsVolumeResult == null) {
			return null;
		}

		if (eventsVolumeResult.events == null) {
			return null;
		}

		return eventsVolumeResult;
	}

	public static List<EventResult> getEvents(ApiClient apiClient, String serviceId, String viewId, DateTime from,
			DateTime to) {
		EventsRequest eventsVolumeRequest = EventsRequest.newBuilder().setServiceId(serviceId).setViewId(viewId)
				.setFrom(from.toString(fmt)).setTo(to.toString(fmt)).build();

		Response<EventsResult> eventsResponse = apiClient.get(eventsVolumeRequest);

		if (eventsResponse.isBadResponse()) {
			return null;
		}

		EventsResult eventsResult = eventsResponse.data;

		if (eventsResult == null) {
			return null;
		}

		if (eventsResult.events == null) {
			return null;
		}

		return eventsResult.events;
	}

	public static Graph getEventsGraph(ApiClient apiClient, String serviceId, String viewId, int pointsCount,
			VolumeType volumeType, DateTime from, DateTime to) {

		GraphResult graphResult = getEventsGraphResult(apiClient, serviceId, viewId, pointsCount, volumeType, from, to);

		if (CollectionUtil.safeIsEmpty(graphResult.graphs)) {
			return null;
		}

		Graph graph = graphResult.graphs.get(0);

		if (!viewId.equals(graph.id)) {
			return null;
		}

		if (CollectionUtil.safeIsEmpty(graph.points)) {
			return null;
		}

		return graph;
	}
	
	public static GraphResult getEventsGraphResult(ApiClient apiClient, String serviceId, String viewId,
			int pointsCount, VolumeType volumeType, DateTime from, DateTime to) {
		return getEventsGraphResult(apiClient, serviceId, viewId, pointsCount, volumeType, from, to, false);
	}

	public static GraphResult getEventsGraphResult(ApiClient apiClient, String serviceId, String viewId,
			int pointsCount, VolumeType volumeType, DateTime from, DateTime to, boolean raw) {

		GraphRequest graphRequest = GraphRequest.newBuilder().setServiceId(serviceId).setViewId(viewId)
				.setGraphType(GraphType.view).setFrom(from.toString(fmt)).setTo(to.toString(fmt))
				.setVolumeType(volumeType).setWantedPointCount(pointsCount).setRaw(raw).build();

		Response<GraphResult> graphResponse = apiClient.get(graphRequest);

		if (graphResponse.isBadResponse()) {
			return null;
		}

		GraphResult graphResult = graphResponse.data;

		if (graphResult == null) {
			return null;
		}

		return graphResult;
	}

	public static void removeView(ApiClient apiClient, String serviceId, String viewId) {
		DeleteViewRequest viewRequest = DeleteViewRequest.newBuilder().setServiceId(serviceId).setViewId(viewId)
				.build();

		Response<EmptyResult> viewResponse = apiClient.delete(viewRequest);

		if (viewResponse.isBadResponse()) {
			System.err.println("Problem removing view " + viewId);
			return;
		}

		System.out.println("Removed view " + viewId);
	}
}
