package com.takipi.common.api.request.server;

import com.takipi.common.api.request.ProcessTagRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.server.ServersResult;

public class ServersRequest extends ProcessTagRequest implements ApiGetRequest<ServersResult> {
	ServersRequest(String serviceId, boolean active) {
		super(serviceId, active);
	}

	@Override
	public Class<ServersResult> resultClass() {
		return ServersResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/servers";
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

		public ServersRequest build() {
			validate();

			return new ServersRequest(serviceId, active);
		}
	}
}
