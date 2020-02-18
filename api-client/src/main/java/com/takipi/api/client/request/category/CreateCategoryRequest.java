package com.takipi.api.client.request.category;

import java.util.HashMap;
import java.util.Map;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.category.CreateCategoryResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

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
	public String postData() {
		Map<String, String> map = new HashMap<>();

		map.put("name", JsonUtil.stringify(name));
		map.put("shared", Boolean.toString(shared));

		return JsonUtil.createSimpleJson(map, false);
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

			if (StringUtil.isNullOrEmpty(name)) {
				throw new IllegalArgumentException("Missing name");
			}
		}

		public CreateCategoryRequest build() {
			validate();

			return new CreateCategoryRequest(serviceId, name, shared);
		}
	}
}
