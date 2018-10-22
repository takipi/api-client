package com.takipi.api.client.request.label;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class CreateLabelRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String name;
	private final String color;

	CreateLabelRequest(String serviceId, String name, String color) {
		super(serviceId);

		this.name = name;
		this.color = color;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/labels";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(2);

		map.put("name", name);

		if (!Strings.isNullOrEmpty(color)) {
			map.put("color", color);
		}

		return JsonUtil.createSimpleJson(map, true).getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String name;
		private String color;

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

		public Builder setColor(String color) {
			this.color = color;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (Strings.isNullOrEmpty(name)) {
				throw new IllegalArgumentException("Missing name");
			}
		}

		public CreateLabelRequest build() {
			validate();

			return new CreateLabelRequest(serviceId, name, color);
		}
	}
}
