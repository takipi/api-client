package com.takipi.api.client.request.functions.settings;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import com.google.common.base.Strings;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPutRequest;
import com.takipi.common.util.JsonUtil;

public class PutFunctionSettingRequest extends FunctionSettingRequest implements ApiPutRequest<EmptyResult> {

	public final String value;

	PutFunctionSettingRequest(String serviceId, String libraryId, String functionId, String key, String value) {
		super(serviceId, libraryId, functionId, key);

		this.value = value;
	}

	@Override
	public byte[] putData() throws UnsupportedEncodingException {
		String data = JsonUtil.createSimpleJson(Collections.singletonMap("value", value), true);

		return data.getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends FunctionSettingRequest.Builder {
		private String value;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setLibraryId(String libraryId) {
			super.setLibraryId(libraryId);

			return this;
		}

		@Override
		public Builder setFunctionId(String functionId) {
			super.setFunctionId(functionId);

			return this;
		}

		@Override
		public Builder setKey(String key) {
			super.setKey(key);

			return this;
		}

		public Builder setValue(String value) {
			this.value = value;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (Strings.isNullOrEmpty(value)) {
				throw new IllegalArgumentException("Missing value");
			}
		}

		public PutFunctionSettingRequest build() {
			validate();

			return new PutFunctionSettingRequest(serviceId, libraryId, functionId, key, value);
		}
	}
}
