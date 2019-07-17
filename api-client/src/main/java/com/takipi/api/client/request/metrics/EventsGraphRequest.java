package com.takipi.api.client.request.metrics;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

import com.takipi.api.client.request.BreakdownEventsTimeframeRequest;
import com.takipi.api.client.request.event.BreakdownType;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphResolution;
import com.takipi.api.client.util.validation.ValidationUtil.GraphType;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventsGraphRequest extends BreakdownEventsTimeframeRequest implements ApiGetRequest<GraphResult> {
	public final GraphType graphType;
	public final VolumeType volumeType;
	public final int wantedPointCount;
	public final GraphResolution resolution;

	EventsGraphRequest(String serviceId, Collection<String> eventIds, GraphType graphType, VolumeType volumeType,
			String from, String to, int wantedPointCount, GraphResolution resolution, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean breakServers, boolean breakApps,
			boolean breakDeployments) {
		super(serviceId, eventIds, from, to, servers, apps, deployments, breakServers, breakApps, breakDeployments);

		this.graphType = graphType;
		this.volumeType = volumeType;
		this.wantedPointCount = wantedPointCount;
		this.resolution = resolution;
	}

	@Override
	public Class<GraphResult> resultClass() {
		return GraphResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/metrics/" + graphType.name() + "/graph";
	}

	@Override
	protected int paramsCount() {
		// One slot for the points count / resolution.
		//
		return super.paramsCount() + 1 + (volumeType != null ? 1 : 0);
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		if (resolution != null) {
			params[index++] = "resolution=" + resolution.name();
		} else {
			params[index++] = "points=" + String.valueOf(wantedPointCount);
		}

		if (volumeType != null) {
			params[index++] = "stats=" + volumeType.name();
		}

		return index;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends BreakdownEventsTimeframeRequest.Builder {
		private GraphType graphType;
		private VolumeType volumeType;
		private int wantedPointCount;
		private GraphResolution resolution;

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder addEventId(String eventId) {
			super.addEventId(eventId);

			return this;
		}

		public Builder setGraphType(GraphType graphType) {
			this.graphType = graphType;

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

		public Builder setWantedPointCount(int wantedPointCount) {
			this.wantedPointCount = wantedPointCount;

			return this;
		}

		public Builder setResolution(GraphResolution resolution) {
			this.resolution = resolution;

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
		public Builder setBreakFilters(Set<BreakdownType> breakdownTypes) {
			super.setBreakFilters(breakdownTypes);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if ((resolution == null) && (wantedPointCount <= 0)) {
				throw new IllegalArgumentException(
						"Missing graph resolution with illegal wanted point count - " + wantedPointCount);
			}

			if (graphType == null) {
				throw new IllegalArgumentException("Missing graph type");
			}
		}

		public EventsGraphRequest build() {
			validate();

			return new EventsGraphRequest(serviceId, eventIds, graphType, volumeType, from, to, wantedPointCount,
					resolution, servers, apps, deployments, breakServers, breakApps, breakDeployments);
		}
	}
}
