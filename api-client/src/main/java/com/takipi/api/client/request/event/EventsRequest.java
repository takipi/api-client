package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.common.util.CollectionUtil;

public class EventsRequest extends ViewTimeframeRequest implements ApiGetRequest<EventsResult> {
	public final boolean includeStacktrace;
	public final boolean breakServers;
	public final boolean breakApps;
	public final boolean breakDeployments;

	EventsRequest(String serviceId, String viewId, String from, String to, boolean raw, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean includeStacktrace, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.includeStacktrace = includeStacktrace;
		this.breakServers = breakServers;
		this.breakApps = breakApps;
		this.breakDeployments = breakDeployments;
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

	@Override
	protected int paramsCount() {
		// One slot for the stacktace flag and three for breakdown.
		//
		return super.paramsCount() + 4;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stacktrace=" + Boolean.toString(includeStacktrace);

		params[index++] = "breakServers=" + Boolean.toString(breakServers);
		params[index++] = "breakApps=" + Boolean.toString(breakApps);
		params[index++] = "breakDeployments=" + Boolean.toString(breakDeployments);

		return index;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ViewTimeframeRequest.Builder {
		private boolean includeStacktrace;
		private boolean breakServers;
		private boolean breakApps;
		private boolean breakDeployments;

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

		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			this.includeStacktrace = includeStacktrace;

			return this;
		}

		public Builder setBreakServers(boolean breakServers) {
			this.breakServers = breakServers;

			return this;
		}

		public Builder setBreakApps(boolean breakApps) {
			this.breakApps = breakApps;

			return this;
		}

		public Builder setBreakDeployments(boolean breakDeployments) {
			this.breakDeployments = breakDeployments;

			return this;
		}

		public EventsRequest build() {
			validate();

			return new EventsRequest(serviceId, viewId, from, to, raw, servers, apps, deployments, includeStacktrace,
					breakServers, breakApps, breakDeployments);
		}
		
		public void applyBreakFilter()
		{
			if (!CollectionUtil.safeIsEmpty(servers)) {
				setBreakServers(true);
			}
			
			if (!CollectionUtil.safeIsEmpty(apps)) {
				setBreakApps(true);
			}
			
			if (!CollectionUtil.safeIsEmpty(deployments)) {
				setBreakDeployments(true);
			}
		}
	}
}
