package com.takipi.api.client.request.process;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.process.StatusResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class StatusRequest extends BaseStatusRequest implements ApiGetRequest<StatusResult> {

	StatusRequest(String serviceId, boolean connected) {
		super(serviceId, connected, "");
	}

	@Override
	public Class<StatusResult> resultClass() {
		return StatusResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private boolean connected;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setConnected(boolean connected) {
			this.connected = connected;

			return this;
		}

		public StatusRequest build() {
			validate();

			return new StatusRequest(serviceId, connected);
		}
	}
}