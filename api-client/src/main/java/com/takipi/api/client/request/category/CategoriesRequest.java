package com.takipi.api.client.request.category;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.category.CategoriesResult;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.common.util.StringUtil;

public class CategoriesRequest extends ServiceRequest implements ApiGetRequest<CategoriesResult> {
	public final String categoryName;
	public final boolean includeViews;

	CategoriesRequest(String serviceId, String categoryName, boolean includeViews) {
		super(serviceId);

		this.categoryName = categoryName;
		this.includeViews = includeViews;
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
	public String[] queryParams() throws UnsupportedEncodingException {
		int size = (StringUtil.isNullOrEmpty(categoryName) ? 1 : 2);

		String[] params = new String[size];

		params[0] = "views=" + Boolean.toString(includeViews);

		if (!StringUtil.isNullOrEmpty(categoryName)) {
			params[1] = "name=" + encode(categoryName);
		}

		return params;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String categoryName;
		private boolean includeViews;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setCategoryName(String categoryName) {
			this.categoryName = categoryName;

			return this;
		}

		public Builder setIncludeViews(boolean includeViews) {
			this.includeViews = includeViews;

			return this;
		}

		public CategoriesRequest build() {
			validate();

			return new CategoriesRequest(serviceId, categoryName, includeViews);
		}
	}
}
