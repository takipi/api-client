package com.takipi.api.client.request.process;

import com.takipi.api.client.result.process.AgentsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class AgentsRequest extends BaseStatusRequest implements ApiGetRequest<AgentsResult> {

	AgentsRequest(String serviceId, boolean connected) {
		super(serviceId, connected, "agents");
	}

	@Override
	public Class<AgentsResult> resultClass() {
		return AgentsResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends BaseStatusRequest.Builder {
		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setConnected(boolean connected) {
			super.setConnected(connected);

			return this;
		}

		public AgentsRequest build() {
			validate();

			return new AgentsRequest(serviceId, connected);
		}
	}
}