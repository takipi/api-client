package com.takipi.api.client.request.metrics;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphResolution;
import com.takipi.api.client.util.validation.ValidationUtil.GraphType;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GraphRequest extends ViewTimeframeRequest implements ApiGetRequest<GraphResult> {
	public final GraphType graphType;
	public final VolumeType volumeType;
	public final int wantedPointCount;
	public final GraphResolution resolution;
	public final boolean breakdown;
	
	GraphRequest(String serviceId, String viewId, GraphType graphType, VolumeType volumeType, String from, String to,
			boolean raw, int wantedPointCount, GraphResolution resolution, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean breakdown) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.graphType = graphType;
		this.volumeType = volumeType;
		this.wantedPointCount = wantedPointCount;
		this.resolution = resolution;
		this.breakdown = breakdown;
	}

	@Override
	public Class<GraphResult> resultClass() {
		return GraphResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/metrics/" + graphType.name() + "/graph";
	}

	@Override
	protected int paramsCount() {
		// One slot for the points count / resolution and one for breakdown.
		//
		return super.paramsCount() + 2 + (volumeType != null ? 1 : 0);
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
		
		params[index++] = "breakdown=" + Boolean.toString(breakdown);

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
		private GraphType graphType;
		private VolumeType volumeType;
		private int wantedPointCount;
		private GraphResolution resolution;
		private boolean breakdown;
		
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
		
		public Builder setBreakdown(boolean breakdown) {
			this.breakdown = breakdown;
			
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

		public GraphRequest build() {
			validate();

			return new GraphRequest(serviceId, viewId, graphType, volumeType, from, to, raw, wantedPointCount,
					resolution, servers, apps, deployments, breakdown);
		}
	}
}
