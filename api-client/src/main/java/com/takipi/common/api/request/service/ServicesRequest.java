package com.takipi.common.api.request.service;

import com.takipi.common.api.request.BaseRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.service.ServicesResult;

public class ServicesRequest extends BaseRequest implements ApiGetRequest<ServicesResult> {
	ServicesRequest() {

	}

	@Override
	public Class<ServicesResult> resultClass() {
		return ServicesResult.class;
	}

	@Override
	public String urlPath() {
		return "/services";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		Builder() {

		}

		public ServicesRequest build() {
			return new ServicesRequest();
		}
	}
}
