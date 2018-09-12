package com.takipi.common.api.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.result.intf.ApiResult;

public interface ApiPutRequest<T extends ApiResult> extends ApiRequest {
	String[] getParams() throws UnsupportedEncodingException;

	byte[] getData() throws UnsupportedEncodingException;

	Class<T> resultClass();
}
