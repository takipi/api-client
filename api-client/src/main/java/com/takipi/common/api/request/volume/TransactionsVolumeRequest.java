package com.takipi.common.api.request.volume;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.common.api.request.TimeframeRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.volume.TransactionsVolumeResult;

public class TransactionsVolumeRequest extends TimeframeRequest implements ApiGetRequest<TransactionsVolumeResult> {
	TransactionsVolumeRequest(String serviceId, String viewId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId, viewId, from, to, servers, apps, deployments);
	}

	@Override
	public Class<TransactionsVolumeResult> resultClass() {
		return TransactionsVolumeResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/entrypoints/";
	}

	@Override
	public String[] getParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends TimeframeRequest.Builder {
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

		public TransactionsVolumeRequest build() {
			validate();

			return new TransactionsVolumeRequest(serviceId, viewId, from, to, servers, apps, deployments);
		}
	}
}
