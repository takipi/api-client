package com.takipi.api.client.request.view;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.util.validation.ValidationUtil;
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

	@Override
	public String postData() {
		return null;
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

			if (!ValidationUtil.isLegalViewId(viewId)) {
				throw new IllegalArgumentException("Illegal view id - " + viewId);
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
