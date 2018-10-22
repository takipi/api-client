package com.takipi.api.client.request.application;

import com.takipi.api.client.request.ProcessTagRequest;
import com.takipi.api.client.result.application.ApplicationsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

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
