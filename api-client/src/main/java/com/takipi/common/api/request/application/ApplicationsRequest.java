package com.takipi.common.api.request.application;

import com.takipi.common.api.request.ProcessTagRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.application.ApplicationsResult;

public class ApplicationsRequest extends ProcessTagRequest implements ApiGetRequest<ApplicationsResult> {
	ApplicationsRequest(String serviceId, boolean active) {
		super(serviceId, active);
	}

	@Override
	public Class<ApplicationsResult> resultClass() {
		return ApplicationsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/applications";
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

		public ApplicationsRequest build() {
			validate();

			return new ApplicationsRequest(serviceId, active);
		}
	}
}
