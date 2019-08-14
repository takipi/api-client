package com.takipi.api.client.request.view;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.view.ViewResult;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class ViewRequest extends ServiceRequest implements ApiGetRequest<ViewResult> {
	public final String viewId;

	ViewRequest(String serviceId, String viewId) {
		super(serviceId);

		this.viewId = viewId;
	}

	@Override
	public Class<ViewResult> resultClass() {
		return ViewResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
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

		public ViewRequest build() {
			validate();

			return new ViewRequest(serviceId, viewId);
		}
	}
}
