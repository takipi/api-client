package com.takipi.common.api.request.functions;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.functions.FunctionsResult;

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
