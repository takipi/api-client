package com.takipi.api.client.request.alert;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.GenericResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.ValidationUtil;

public abstract class AlertRequest extends ServiceRequest implements ApiPostRequest<GenericResult> {
	private final String viewId;

	protected AlertRequest(String serviceId, String viewId) {
		super(serviceId);

		this.viewId = viewId;
	}

	@Override
	protected String baseUrlPath() {
		return super.baseUrlPath() + "/views/" + viewId;
	}

	@Override
	public Class<GenericResult> resultClass() {
		return GenericResult.class;
	}

	public static class Builder extends ServiceRequest.Builder {
		protected String viewId;

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setViewId(String viewId) {
			this.viewId = viewId;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalViewId(viewId)) {
				throw new IllegalArgumentException("Illegal view id - " + viewId);
			}
		}
	}
}
