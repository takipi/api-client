package com.takipi.common.api.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.result.intf.ApiResult;

public interface ApiPostRequest<T extends ApiResult> extends ApiRequest {
	byte[] postData() throws UnsupportedEncodingException;

	Class<T> resultClass();
}
