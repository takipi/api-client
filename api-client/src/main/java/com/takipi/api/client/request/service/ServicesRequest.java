package com.takipi.api.client.request.service;

import com.takipi.api.client.request.BaseRequest;
import com.takipi.api.client.result.service.ServicesResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

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
