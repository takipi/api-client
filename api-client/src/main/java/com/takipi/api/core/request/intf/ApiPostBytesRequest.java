package com.takipi.api.core.request.intf;

import com.takipi.api.core.result.intf.ApiResult;

public interface ApiPostBytesRequest<T extends ApiResult> extends ApiRequest {
	public byte[] postData();

	public Class<T> resultClass();
}
