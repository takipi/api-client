package com.takipi.api.client.request.functions;

import com.takipi.api.client.request.GlobalSettingsRequest;
import com.takipi.api.client.result.functions.FunctionsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GlobalFunctionsRequest extends GlobalSettingsRequest implements ApiGetRequest<FunctionsResult> {
	GlobalFunctionsRequest() {
		super();
	}

	@Override
	public Class<FunctionsResult> resultClass() {
		return FunctionsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/udfs";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends GlobalSettingsRequest.Builder {
		Builder() {

		}

		public GlobalFunctionsRequest build() {
			validate();

			return new GlobalFunctionsRequest();
		}
	}
}
