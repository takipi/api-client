package com.takipi.api.client.request.view;

import com.google.common.base.Strings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiDeleteRequest;

public class DeleteViewRequest extends ServiceRequest implements ApiDeleteRequest<EmptyResult> {
	private final String viewId;

	DeleteViewRequest(String serviceId, String viewId) {
		super(serviceId);

		this.viewId = viewId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String viewId;

		Builder() {

		}

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

			if (viewId == null) {
				throw new IllegalArgumentException("Missing view");
			}

			if (Strings.isNullOrEmpty(viewId)) {
				throw new IllegalArgumentException("View must contain value");
			}
		}

		public DeleteViewRequest build() {
			validate();

			return new DeleteViewRequest(serviceId, viewId);
		}
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}
}
