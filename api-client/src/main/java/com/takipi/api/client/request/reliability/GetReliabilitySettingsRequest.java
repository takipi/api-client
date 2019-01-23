package com.takipi.api.client.request.reliability;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.reliability.GetReliabilitySettingsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GetReliabilitySettingsRequest extends ServiceRequest
		implements ApiGetRequest<GetReliabilitySettingsResult> {
	protected GetReliabilitySettingsRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<GetReliabilitySettingsResult> resultClass() {
		return GetReliabilitySettingsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/reliability-settings";
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

		public GetReliabilitySettingsRequest build() {
			validate();

			return new GetReliabilitySettingsRequest(serviceId);
		}
	}
}
