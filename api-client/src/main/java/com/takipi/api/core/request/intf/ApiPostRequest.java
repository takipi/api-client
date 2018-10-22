package com.takipi.api.core.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.api.core.result.intf.ApiResult;

public interface ApiPostRequest<T extends ApiResult> extends ApiRequest {
	public byte[] postData() throws UnsupportedEncodingException;

	public Class<T> resultClass();
}
