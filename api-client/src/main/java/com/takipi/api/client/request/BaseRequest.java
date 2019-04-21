package com.takipi.api.client.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiRequest;

public abstract class BaseRequest implements ApiRequest {
	protected BaseRequest() {

	}

	protected static String encode(String param) throws UnsupportedEncodingException {
		return URLEncoder.encode(param, ApiConstants.UTF8_ENCODING);
	}

	@Override
	public String contentType() {
		return ApiConstants.CONTENT_TYPE_JSON;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return null;
	}
}
