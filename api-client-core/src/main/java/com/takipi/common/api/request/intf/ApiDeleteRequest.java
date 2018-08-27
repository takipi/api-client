package com.takipi.common.api.request.intf;

import com.takipi.common.api.result.intf.ApiResult;

public interface ApiDeleteRequest<T extends ApiResult> extends ApiRequest {
	public Class<T> resultClass();
}
