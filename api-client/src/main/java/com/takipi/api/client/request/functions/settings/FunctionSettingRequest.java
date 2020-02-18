package com.takipi.api.client.request.functions.settings;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.common.util.StringUtil;

public abstract class FunctionSettingRequest extends ServiceRequest {

	public final String libraryId;
	public final String functionId;
	public final String key;

	FunctionSettingRequest(String serviceId, String libraryId, String functionId, String key) {
		super(serviceId);

		this.libraryId = libraryId;
		this.functionId = functionId;
		this.key = key;
	}

	@Override
	public String urlPath() throws UnsupportedEncodingException {
		return baseUrlPath() + "/udfs/settings/" + encode(libraryId) + "/" + encode(functionId) + "/" + encode(key);
	}

	public static class Builder extends ServiceRequest.Builder {
		String libraryId;
		String functionId;
		String key;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setLibraryId(String libraryId) {
			this.libraryId = libraryId;

			return this;
		}

		public Builder setFunctionId(String functionId) {
			this.functionId = functionId;

			return this;
		}

		public Builder setKey(String key) {
			this.key = key;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalLibraryId(libraryId)) {
				throw new IllegalArgumentException("Illegal library id - " + libraryId);
			}

			if (StringUtil.isNullOrEmpty(functionId)) {
				throw new IllegalArgumentException("Missing function id");
			}

			if (StringUtil.isNullOrEmpty(key)) {
				throw new IllegalArgumentException("Missing key");
			}
		}
	}
}
