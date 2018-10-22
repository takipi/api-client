package com.takipi.api.client.request.transaction;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.transaction.TransactionsGraphResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class TransactionsGraphRequest extends ViewTimeframeRequest implements ApiGetRequest<TransactionsGraphResult> {
	public final int wantedPointCount;

	TransactionsGraphRequest(String serviceId, String viewId, String from, String to, int wantedPointCount,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments) {
		super(serviceId, viewId, from, to, servers, apps, deployments);

		this.wantedPointCount = wantedPointCount;
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

		public TransactionsGraphRequest build() {
			validate();

			return new TransactionsGraphRequest(serviceId, viewId, from, to, wantedPointCount, servers, apps,
					deployments);
		}
	}
}
