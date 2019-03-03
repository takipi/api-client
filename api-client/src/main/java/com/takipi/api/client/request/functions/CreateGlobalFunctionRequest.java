package com.takipi.api.client.request.functions;

import com.takipi.api.client.request.GlobalSettingsRequest;
import com.takipi.api.client.result.functions.CreateFunctionResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostBytesRequest;

public class CreateGlobalFunctionRequest extends GlobalSettingsRequest
		implements ApiPostBytesRequest<CreateFunctionResult> {
	private final byte[] data;

	CreateGlobalFunctionRequest(byte[] data) {
		super();

		this.data = data;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/udfs";
	}

	@Override
	public byte[] postData() {
		return data;
	}

	@Override
	public String contentType() {
		return ApiConstants.CONTENT_TYPE_BINARY;
	}

	@Override
	public Class<CreateFunctionResult> resultClass() {
		return CreateFunctionResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends GlobalSettingsRequest.Builder {
		private byte[] data;

		Builder() {

		}

		public Builder setData(byte[] data) {
			this.data = data;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if ((data == null) || (data.length == 0)) {
				throw new IllegalArgumentException("Missing data");
			}
		}

		public CreateGlobalFunctionRequest build() {
			validate();

			return new CreateGlobalFunctionRequest(data);
		}
	}
}
