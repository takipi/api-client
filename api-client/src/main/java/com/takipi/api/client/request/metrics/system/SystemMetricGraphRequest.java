package com.takipi.api.client.request.metrics.system;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.TimeframeRequest;
import com.takipi.api.client.result.metrics.system.SystemMetricGraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphResolution;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class SystemMetricGraphRequest extends TimeframeRequest implements ApiGetRequest<SystemMetricGraphResult> {
	public final String metricName;
	public final int wantedPointCount;
	public final GraphResolution resolution;

	SystemMetricGraphRequest(String serviceId, String metricName, String from, String to, int wantedPointCount,
			GraphResolution resolution, Collection<String> servers, Collection<String> apps,
			Collection<String> deployments) {
		super(serviceId, from, to, servers, apps, deployments);

		this.metricName = metricName;
		this.wantedPointCount = wantedPointCount;
		this.resolution = resolution;
	}

	@Override
	public Class<SystemMetricGraphResult> resultClass() {
		return SystemMetricGraphResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/metrics/system/" + metricName + "/graph";
	}

	@Override
	protected int paramsCount() {
		// One slot for the points count / resolution.
		//
		return super.paramsCount() + 1;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		if (resolution != null) {
			params[index++] = "resolution=" + resolution.name();
		} else {
			params[index++] = "points=" + String.valueOf(wantedPointCount);
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

	public static class Builder extends TimeframeRequest.Builder {
		private String metricName;
		private int wantedPointCount;
		private GraphResolution resolution;

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setMetricName(String metricName) {
			this.metricName = metricName;

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
		}

		public SystemMetricGraphRequest build() {
			validate();

			return new SystemMetricGraphRequest(serviceId, metricName, from, to, wantedPointCount, resolution, servers,
					apps, deployments);
		}
	}
}
