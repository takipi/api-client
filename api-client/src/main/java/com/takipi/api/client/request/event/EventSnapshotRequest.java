package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.EventTimeframeRequest;
import com.takipi.api.client.result.event.EventSnapshotResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventSnapshotRequest extends EventTimeframeRequest implements ApiGetRequest<EventSnapshotResult> {

	EventSnapshotRequest(String serviceId, String eventId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId, eventId, from, to, servers, apps, deployments);
	}
	
	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/snapshot";
	}

	@Override
	public Class<EventSnapshotResult> resultClass() {
		return EventSnapshotResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends EventTimeframeRequest.Builder {

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setEventId(String eventId) {
			super.setEventId(eventId);

			return this;
		}

		@Override
		public Builder setFrom(String from) {
			super.setFrom(from);

			return this;
		}

		@Override
		public Builder setTo(String to) {
			super.setTo(to);

			return this;
		}

		@Override
		public Builder addServer(String server) {
			super.addServer(server);

			return this;
		}

		@Override
		public Builder addApp(String app) {
			super.addApp(app);

			return this;
		}

		@Override
		public Builder addDeployment(String deployment) {
			super.addDeployment(deployment);

			return this;
		}
		
		public EventSnapshotRequest build() {
			validate();

			return new EventSnapshotRequest(serviceId, eventId, from, to, servers, apps, deployments);		
		}
	}
}
