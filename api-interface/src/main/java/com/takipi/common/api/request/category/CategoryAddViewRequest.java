package com.takipi.common.api.request.category;

import java.io.UnsupportedEncodingException;

import com.google.common.collect.ImmutableMap;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;
import com.takipi.common.api.util.JsonUtil;

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
	public byte[] postData() throws UnsupportedEncodingException {
		String json = JsonUtil.createSimpleJson(ImmutableMap.of("view_id", viewId), true);

		return json.getBytes(ApiConstants.UTF8_ENCODING);
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
