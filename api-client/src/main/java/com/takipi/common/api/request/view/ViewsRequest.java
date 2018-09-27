package com.takipi.common.api.request.view;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Strings;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.view.ViewsResult;

public class ViewsRequest extends ServiceRequest implements ApiGetRequest<ViewsResult> {
	public final String viewName;

	ViewsRequest(String serviceId, String viewName) {
		super(serviceId);

		this.viewName = viewName;
	}

	@Override
	public Class<ViewsResult> resultClass() {
		return ViewsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views";
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		if (Strings.isNullOrEmpty(viewName)) {
			return null;
		}

		String[] params = new String[1];

		params[0] = "name=" + encode(viewName);

		return params;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String viewName;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setViewName(String viewName) {
			this.viewName = viewName;

			return this;
		}

		public ViewsRequest build() {
			validate();

			return new ViewsRequest(serviceId, viewName);
		}
	}
}
