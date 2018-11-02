package com.takipi.api.client.util.event;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.request.event.EventSnapshotRequest;
import com.takipi.api.client.result.event.EventSnapshotResult;
import com.takipi.api.core.url.UrlClient.Response;

public class EventUtil {
	
	public static int DEFAULT_PERIOD = (int)TimeUnit.DAYS.toMinutes(30);
	
	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yy-hh-mm");

	public static String getEventRecentLinkDefault(ApiClient apiClient, String serviceId, 
			String eventId, DateTime from, DateTime to, Collection<String> applications, Collection<String> servers,
			Collection<String> deployments, int defaultSpan) {
		
		String result = getEventRecentLink(apiClient, serviceId, eventId, 
			from, to, applications, servers, deployments);
		
		if (result == null) {
			
			DateTime now = DateTime.now();
			
			result =  getEventRecentLink(apiClient, serviceId, eventId, 
					now.minusMinutes(defaultSpan), now, null, null, null);
		}
		
		return result;
	}
	
	public static String getEventRecentLink(ApiClient apiClient, String serviceId, 
		String eventId, DateTime from, DateTime to, Collection<String> applications, Collection<String> servers,
		Collection<String> deployments) {
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

		EventSnapshotRequest.Builder builder = EventSnapshotRequest.newBuilder().setServiceId(serviceId)
				.setEventId(eventId).setFrom(from.toString(fmt)).setTo(to.toString(fmt));

		if (applications != null) {
			for (String app : applications) {
				builder.addApp(app);
			}
		}
		
		if (servers != null) {
			for (String srv : servers) {
				builder.addApp(srv);
			}
		}
		
		if (deployments != null) {
			for (String dep : deployments) {
				builder.addDeployment(dep);
			}
		}
		
		Response<EventSnapshotResult> response = apiClient.get(builder.build());

		if ((response.isBadResponse()) || (response.data == null)) {
			return null;
		}
		
		String link = response.data.link;
		
		if (link == null) {
			return null;
		}				
		
		DateTime now = DateTime.now();
		
		StringBuilder result = new StringBuilder(link);
		result.append("&timeframe=custom&from=");
		result.append(from.toString(formatter));
		result.append("&to=");
		result.append(now.toString(formatter));


		return result.toString();
	}
}
