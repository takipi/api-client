package com.takipi.common.api.request.timer;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.timer.TimersResult;

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

		public TimersRequest build() {
			validate();

			return new TimersRequest(serviceId);
		}
	}
}
