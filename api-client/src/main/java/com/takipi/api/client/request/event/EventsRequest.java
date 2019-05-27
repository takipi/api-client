package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventsRequest extends BaseEventsRequest implements ApiGetRequest<EventsResult> {

	EventsRequest(String serviceId, String viewId, String from, String to, boolean raw, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean includeStacktrace, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		
		super(serviceId, viewId, from, to, raw, servers, apps, deployments, 
				includeStacktrace, breakServers, breakApps, breakDeployments);
	}

	@Override
	public Class<EventsResult> resultClass() {
		return EventsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/events";
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends BaseEventsRequest.Builder {
		

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setViewId(String viewId) {
			super.setViewId(viewId);

			return this;
		}

		@Override
		public Builder setRaw(boolean raw) {
			super.setRaw(raw);

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

		@Override
		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			super.setIncludeStacktrace(includeStacktrace);

			return this;
		}

		@Override
		public Builder setBreakServers(boolean breakServers) {
			super.setBreakServers(breakServers);

			return this;
		}

		@Override
		public Builder setBreakApps(boolean breakApps) {
			super.setBreakApps(breakApps);

			return this;
		}

		@Override
		public Builder setBreakDeployments(boolean breakDeployments) {
			super.setBreakDeployments(breakDeployments);

			return this;
		}
		
		@Override
		public EventsRequest build() {
			validate();

			return new EventsRequest(serviceId, viewId, from, to, raw, servers, apps, deployments, includeStacktrace,
					breakServers, breakApps, breakDeployments);
		}
	}
}
