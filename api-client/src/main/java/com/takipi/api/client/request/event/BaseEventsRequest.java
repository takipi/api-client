package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.BreakdownViewTimeframeRequest;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public abstract class BaseEventsRequest extends BreakdownViewTimeframeRequest {
	public final boolean includeStacktrace;

	BaseEventsRequest(String serviceId, String viewId, String from, String to, boolean raw, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean includeStacktrace, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments, breakServers, breakApps, breakDeployments);

		this.includeStacktrace = includeStacktrace;
	}

	@Override
	protected int paramsCount() {
		// One slot for the stacktace flag and three for breakdown.
		//
		return super.paramsCount() + 1;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stacktrace=" + Boolean.toString(includeStacktrace);

		return index;
	}

	public static abstract class Builder extends BreakdownViewTimeframeRequest.Builder {
		protected boolean includeStacktrace;

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
		
		public Builder setBreakServers(boolean breakServers) {
			super.setBreakServers(breakServers);
			
			return this;
		}
		
		public Builder setBreakApps(boolean breakApps) {
			super.setBreakApps(breakApps);
			
			return this;
		}
		
		public Builder setBreakDeployments(boolean breakDeployments) {
			super.setBreakDeployments(breakDeployments);
			
			return this;
		}

		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			this.includeStacktrace = includeStacktrace;

			return this;
		}

		public abstract ApiGetRequest<EventsResult> build();
	}
}
