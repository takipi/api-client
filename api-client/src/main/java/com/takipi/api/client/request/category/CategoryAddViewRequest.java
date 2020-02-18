package com.takipi.api.client.request.category;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;

public class CategoryAddViewRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String categoryId;
	private final String viewId;

	CategoryAddViewRequest(String serviceId, String categoryId, String viewId) {
		super(serviceId);

		this.categoryId = categoryId;
		this.viewId = viewId;
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/categories/" + categoryId + "/views/";
	}

	@Override
	public String postData() {
		return JsonUtil.createSimpleJson(CollectionUtil.mapOf("view_id", viewId), true);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String categoryId;
		private String viewId;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setCategoryId(String categoryId) {
			this.categoryId = categoryId;

			return this;
		}

		public Builder setViewId(String viewId) {
			this.viewId = viewId;

			return this;
		}

		public CategoryAddViewRequest build() {
			validate();

			return new CategoryAddViewRequest(serviceId, categoryId, viewId);
		}
	}
}
