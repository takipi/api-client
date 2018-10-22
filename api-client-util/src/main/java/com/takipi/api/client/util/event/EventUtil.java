package com.takipi.api.client.util.event;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.request.event.EventSnapshotRequest;
import com.takipi.api.client.result.event.EventSnapshotResult;
import com.takipi.api.core.url.UrlClient.Response;

public class EventUtil {
	public static String getEventRecentLink(ApiClient apiClient, String serviceId, String eventId, int timeSpan) {
		DateTime to = DateTime.now();
		DateTime from = to.minusMinutes(timeSpan);

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

		EventSnapshotRequest eventsSnapshotRequest = EventSnapshotRequest.newBuilder().setServiceId(serviceId)
				.setEventId(eventId).setFrom(from.toString(fmt)).setTo(to.toString(fmt)).build();

		Response<EventSnapshotResult> eventSnapshotResult = apiClient.get(eventsSnapshotRequest);

		if ((eventSnapshotResult.isBadResponse()) || (eventSnapshotResult.data == null)) {
			return null;
		}

		return eventSnapshotResult.data.link;
	}
}
