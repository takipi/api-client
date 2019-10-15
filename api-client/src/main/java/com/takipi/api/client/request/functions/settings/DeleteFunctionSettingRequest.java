package com.takipi.api.client.request.functions.settings;

import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiDeleteRequest;

public class DeleteFunctionSettingRequest extends FunctionSettingRequest implements ApiDeleteRequest<EmptyResult> {

	DeleteFunctionSettingRequest(String serviceId, String libraryId, String functionId, String key) {
		super(serviceId, libraryId, functionId, key);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	@Override
	public String postData() {
		return null;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends FunctionSettingRequest.Builder {
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

		public DeleteFunctionSettingRequest build() {
			validate();

			return new DeleteFunctionSettingRequest(serviceId, libraryId, functionId, key);
		}
	}
}
