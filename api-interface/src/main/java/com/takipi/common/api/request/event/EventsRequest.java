package com.takipi.common.api.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.common.api.request.ViewTimeframeRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.event.EventsResult;

public class EventsRequest extends ViewTimeframeRequest implements ApiGetRequest<EventsResult> {
	EventsRequest(String serviceId, String viewId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId, viewId, from, to, servers, apps, deployments);
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
	public String[] getParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ViewTimeframeRequest.Builder {
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

		public EventsRequest build() {
			validate();

			return new EventsRequest(serviceId, viewId, from, to, servers, apps, deployments);
		}
	}
}
