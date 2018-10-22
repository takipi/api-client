package com.takipi.api.client.request.redaction;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.redaction.CodeRedactionElements;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class CodeRedactionExcludeRequest extends ServiceRequest implements ApiGetRequest<CodeRedactionElements> {
	CodeRedactionExcludeRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<CodeRedactionElements> resultClass() {
		return CodeRedactionElements.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/code-redaction/exclude";
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

		public CodeRedactionExcludeRequest build() {
			validate();

			return new CodeRedactionExcludeRequest(serviceId);
		}
	}
}
