package com.takipi.common.api.request.category;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.category.CategoriesResult;

public class CategoriesRequest extends ServiceRequest implements ApiGetRequest<CategoriesResult> {
	CategoriesRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<CategoriesResult> resultClass() {
		return CategoriesResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/categories";
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

		public CategoriesRequest build() {
			validate();

			return new CategoriesRequest(serviceId);
		}
	}
}
