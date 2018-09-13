package com.takipi.common.api.request.functions;

import com.takipi.common.api.request.GlobalSettingsRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.functions.FunctionsResult;

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

	@Override
	public String[] getParams() {
		return null;
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
