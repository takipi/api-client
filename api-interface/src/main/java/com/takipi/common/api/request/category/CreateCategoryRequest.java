package com.takipi.common.api.request.category;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.category.CreateCategoryResult;
import com.takipi.common.api.util.JsonUtil;

public class CreateCategoryRequest extends ServiceRequest implements ApiPostRequest<CreateCategoryResult> {
	private final String name;
	private final boolean shared;

	CreateCategoryRequest(String serviceId, String name, boolean shared) {
		super(serviceId);

		this.name = name;
		this.shared = shared;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/categories";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(2);

		map.put("name", JsonUtil.stringify(name));
		map.put("shared", Boolean.toString(shared));

		return JsonUtil.createSimpleJson(map, false).getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<CreateCategoryResult> resultClass() {
		return CreateCategoryResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String name;
		private boolean shared;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setName(String name) {
			this.name = name;

			return this;
		}

		public Builder setShared(boolean shared) {
			this.shared = shared;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (Strings.isNullOrEmpty(name)) {
				throw new IllegalArgumentException("Missing name");
			}
		}

		public CreateCategoryRequest build() {
			validate();

			return new CreateCategoryRequest(serviceId, name, shared);
		}
	}
}
