package com.takipi.common.api.request.metrics;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.common.api.request.ViewTimeframeRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.metrics.GraphResult;
import com.takipi.common.api.util.ValidationUtil.GraphType;

public class GraphRequest extends ViewTimeframeRequest implements ApiGetRequest<GraphResult> {
	public final GraphType graphType;
	public final int wantedPointCount;

	GraphRequest(String serviceId, String viewId, GraphType graphType, String from, String to, int wantedPointCount,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments) {
		super(serviceId, viewId, from, to, servers, apps, deployments);

		this.graphType = graphType;
		this.wantedPointCount = wantedPointCount;
	}

	@Override
	public Class<GraphResult> resultClass() {
		return GraphResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/metrics/" + graphType + "/graph";
	}

	@Override
	protected int paramsCount() {
		// One slot for the points count.
		//
		return super.paramsCount() + 1;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "points=" + String.valueOf(wantedPointCount);

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
		private int wantedPointCount;

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

		public Builder setGraphType(GraphType graphType) {
			this.graphType = graphType;

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

		public Builder setWantedPointCount(int wantedPointCount) {
			this.wantedPointCount = wantedPointCount;

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

			if (wantedPointCount <= 0) {
				throw new IllegalArgumentException("Illegal wanted point count - " + wantedPointCount);
			}
		}

		public GraphRequest build() {
			validate();

			return new GraphRequest(serviceId, viewId, graphType, from, to, wantedPointCount, servers, apps,
					deployments);
		}
	}
}
