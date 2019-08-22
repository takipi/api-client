package com.takipi.api.client.request.process;

import com.takipi.api.client.result.process.CollectorsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class CollectorsRequest extends BaseStatusRequest implements ApiGetRequest<CollectorsResult> {

	CollectorsRequest(String serviceId, boolean connected) {
		super(serviceId, connected, "collectors");
	}

	@Override
	public Class<CollectorsResult> resultClass() {
		return CollectorsResult.class;
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

		public CollectorsRequest build() {
			validate();

			return new CollectorsRequest(serviceId, connected);
		}
	}
}
