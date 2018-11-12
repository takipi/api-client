package com.takipi.api.client.util.view;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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

	public static void createLabelViewsIfNotExists(ApiClient apiClient, String serviceId,
			Collection<Pair<String, String>> viewsAndLabels, String categoryId) {

		Map<String, SummarizedView> views = getServiceViewsByName(apiClient, serviceId);

		for (Pair<String, String> pair : viewsAndLabels) {
			String viewName = pair.getFirst();
			String labelName = pair.getSecond();

			SummarizedView view = views.get(viewName);

			if (view != null) {
				System.out.println("view " + viewName + " found with ID " + view.id);

				continue;
			}

			ViewInfo viewInfo = new ViewInfo();

			viewInfo.name = viewName;
			viewInfo.filters = new ViewFilters();
			viewInfo.filters.labels = Collections.singletonList(labelName);
			viewInfo.shared = true;

			CreateViewRequest createViewRequest = CreateViewRequest.newBuilder().setServiceId(serviceId)
					.setViewInfo(viewInfo).build();

			Response<CreateViewResult> viewResponse = apiClient.post(createViewRequest);

			if ((viewResponse.isBadResponse()) || (viewResponse.data == null)) {
				System.err.println("Cannot create view " + viewName);

				continue;
			}

			System.out.println("Created view " + viewResponse.data.view_id + " for label " + labelName);

			CategoryAddViewRequest categoryAddViewRequest = CategoryAddViewRequest.newBuilder().setServiceId(serviceId)
					.setViewId(viewResponse.data.view_id).setCategoryId(categoryId).build();

			Response<EmptyResult> categoryAddViewResponse = apiClient.post(categoryAddViewRequest);

			if (categoryAddViewResponse.isBadResponse()) {
				System.out.println("Error adding view " + viewName + " to category " + categoryId);
			}
		}
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

		GraphRequest graphRequest = GraphRequest.newBuilder().setServiceId(serviceId).setViewId(viewId)
				.setGraphType(GraphType.view).setFrom(from.toString(fmt)).setTo(to.toString(fmt))
				.setVolumeType(volumeType).setWantedPointCount(pointsCount).build();

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
}
