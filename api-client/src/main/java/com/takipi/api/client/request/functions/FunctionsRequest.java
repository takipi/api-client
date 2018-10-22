package com.takipi.api.client.request.functions;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.functions.FunctionsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class FunctionsRequest extends ServiceRequest implements ApiGetRequest<FunctionsResult> {
	FunctionsRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<FunctionsResult> resultClass() {
		return FunctionsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/udfs";
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

		public FunctionsRequest build() {
			validate();

			return new FunctionsRequest(serviceId);
		}
	}
}
