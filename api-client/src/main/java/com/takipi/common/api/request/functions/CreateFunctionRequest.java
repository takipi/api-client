package com.takipi.common.api.request.functions;

import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.functions.CreateFunctionResult;

public class CreateFunctionRequest extends ServiceRequest implements ApiPostRequest<CreateFunctionResult> {
	private final byte[] data;

	CreateFunctionRequest(String serviceId, byte[] data) {
		super(serviceId);

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

	public static class Builder extends ServiceRequest.Builder {
		private byte[] data;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
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

		public CreateFunctionRequest build() {
			validate();

			return new CreateFunctionRequest(serviceId, data);
		}
	}
}
