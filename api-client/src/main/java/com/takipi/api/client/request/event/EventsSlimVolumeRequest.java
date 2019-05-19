package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.event.EventsSlimVolumeResult;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventsSlimVolumeRequest extends ViewTimeframeRequest implements ApiGetRequest<EventsSlimVolumeResult> {
	public final VolumeType volumeType;
	public final boolean breakServers;
	public final boolean breakApps;
	public final boolean breakDeployments;
	
	EventsSlimVolumeRequest(String serviceId, String viewId, VolumeType volumeType, String from, String to, boolean raw,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.volumeType = volumeType;
		this.breakServers = breakServers;
		this.breakApps = breakApps;
		this.breakDeployments = breakDeployments;
	}

	@Override
	public Class<EventsSlimVolumeResult> resultClass() {
		return EventsSlimVolumeResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/events/stats/";
	}

	@Override
	protected int paramsCount() {
		// One slot for the volume type and three for breakdown.
		//
		return super.paramsCount() + 4;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stats=" + volumeType.toString();
		
		params[index++] = "breakServers=" + Boolean.toString(breakServers);
		params[index++] = "breakApps=" + Boolean.toString(breakApps);
		params[index++] = "breakDeployments=" + Boolean.toString(breakDeployments);
		
		return index;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ViewTimeframeRequest.Builder {
		private VolumeType volumeType;
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

		public Builder setVolumeType(VolumeType volumeType) {
			this.volumeType = volumeType;

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
		
		@Override
		protected void validate() {
			super.validate();

			if (volumeType == null) {
				throw new IllegalArgumentException("Missing volume type");
			}
		}

		public EventsSlimVolumeRequest build() {
			validate();

			return new EventsSlimVolumeRequest(serviceId, viewId, volumeType, from, to, raw, servers, apps,
					deployments, breakServers, breakApps, breakDeployments);
		}
	}
}
