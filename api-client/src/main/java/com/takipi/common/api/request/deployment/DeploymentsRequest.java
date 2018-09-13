package com.takipi.common.api.request.deployment;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.deployment.DeploymentsResult;

public class DeploymentsRequest extends ServiceRequest implements ApiGetRequest<DeploymentsResult> {
	DeploymentsRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<DeploymentsResult> resultClass() {
		return DeploymentsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/deployments";
	}

	@Override
	public String[] getParams() throws UnsupportedEncodingException {
		return null;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public DeploymentsRequest build() {
			validate();

			return new DeploymentsRequest(serviceId);
		}
	}
}
