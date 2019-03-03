package com.takipi.api.client.request.transaction;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.transaction.TransactionsGraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphResolution;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class TransactionsGraphRequest extends ViewTimeframeRequest implements ApiGetRequest<TransactionsGraphResult> {
	public final int wantedPointCount;
	public final GraphResolution resolution;

	TransactionsGraphRequest(String serviceId, String viewId, String from, String to, boolean raw, int wantedPointCount,
			GraphResolution resolution, Collection<String> servers, Collection<String> apps,
			Collection<String> deployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.wantedPointCount = wantedPointCount;
		this.resolution = resolution;
	}

	@Override
	public Class<TransactionsGraphResult> resultClass() {
		return TransactionsGraphResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/entrypoints/graph";
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

	public static class Builder extends ViewTimeframeRequest.Builder {
		private int wantedPointCount;
		private GraphResolution resolution;

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

		public TransactionsGraphRequest build() {
			validate();

			return new TransactionsGraphRequest(serviceId, viewId, from, to, raw, wantedPointCount, resolution, servers,
					apps, deployments);
		}
	}
}
