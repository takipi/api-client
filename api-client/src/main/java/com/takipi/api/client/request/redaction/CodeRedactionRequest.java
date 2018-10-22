package com.takipi.api.client.request.redaction;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.redaction.CodeRedactionResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class CodeRedactionRequest extends ServiceRequest implements ApiGetRequest<CodeRedactionResult> {
	CodeRedactionRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<CodeRedactionResult> resultClass() {
		return CodeRedactionResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/code-redaction";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public CodeRedactionRequest build() {
			validate();

			return new CodeRedactionRequest(serviceId);
		}
	}
}
