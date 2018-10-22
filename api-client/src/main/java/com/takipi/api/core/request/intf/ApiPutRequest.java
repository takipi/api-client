package com.takipi.api.core.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.api.core.result.intf.ApiResult;

public interface ApiPutRequest<T extends ApiResult> extends ApiRequest {
	public byte[] putData() throws UnsupportedEncodingException;

	public Class<T> resultClass();
}
