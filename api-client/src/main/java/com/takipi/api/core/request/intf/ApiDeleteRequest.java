package com.takipi.api.core.request.intf;

import com.takipi.api.core.result.intf.ApiResult;

public interface ApiDeleteRequest<T extends ApiResult> extends ApiRequest {
	public Class<T> resultClass();
}
