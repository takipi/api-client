package com.takipi.common.api.request.intf;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.result.intf.ApiResult;

public interface ApiPutRequest<T extends ApiResult> extends ApiRequest {
	public String[] getParams() throws UnsupportedEncodingException;

	public byte[] getData() throws UnsupportedEncodingException;

	public Class<T> resultClass();
}
