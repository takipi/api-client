package com.takipi.api.client.request.timer;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.timer.TimersResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class TimersRequest extends ServiceRequest implements ApiGetRequest<TimersResult> {
	TimersRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<TimersResult> resultClass() {
		return TimersResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/timers";
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

		public TimersRequest build() {
			validate();

			return new TimersRequest(serviceId);
		}
	}
}
