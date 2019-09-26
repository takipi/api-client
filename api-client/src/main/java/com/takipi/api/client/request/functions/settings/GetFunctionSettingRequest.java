package com.takipi.api.client.request.functions.settings;

import com.takipi.api.client.result.functions.FunctionSettingsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GetFunctionSettingRequest extends FunctionSettingRequest implements ApiGetRequest<FunctionSettingsResult> {

	GetFunctionSettingRequest(String serviceId, String libraryId, String functionId, String key) {
		super(serviceId, libraryId, functionId, key);
	}

	@Override
	public Class<FunctionSettingsResult> resultClass() {
		return FunctionSettingsResult.class;
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

		public GetFunctionSettingRequest build() {
			validate();

			return new GetFunctionSettingRequest(serviceId, libraryId, functionId, key);
		}
	}
}
