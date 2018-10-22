package com.takipi.api.client.request.label;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.label.LabelsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class LabelsRequest extends ServiceRequest implements ApiGetRequest<LabelsResult> {
	LabelsRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/labels/";
	}

	@Override
	public Class<LabelsResult> resultClass() {
		return LabelsResult.class;
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

		public LabelsRequest build() {
			validate();

			return new LabelsRequest(serviceId);
		}
	}
}
