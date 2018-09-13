package com.takipi.common.api.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.result.intf.ApiResult;

public interface ApiPostRequest<T extends ApiResult> extends ApiRequest {
	public byte[] postData() throws UnsupportedEncodingException;

	public Class<T> resultClass();
}
