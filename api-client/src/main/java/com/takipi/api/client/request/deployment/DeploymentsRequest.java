package com.takipi.api.client.request.deployment;

import com.takipi.api.client.request.ProcessTagRequest;
import com.takipi.api.client.result.deployment.DeploymentsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class DeploymentsRequest extends ProcessTagRequest implements ApiGetRequest<DeploymentsResult> {
	DeploymentsRequest(String serviceId, boolean active) {
		super(serviceId, active);
	}

	@Override
	public Class<DeploymentsResult> resultClass() {
		return DeploymentsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/deployments";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ProcessTagRequest.Builder {
		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setActive(boolean active) {
			this.active = active;

			return this;
		}

		public DeploymentsRequest build() {
			validate();

			return new DeploymentsRequest(serviceId, active);
		}
	}
}
