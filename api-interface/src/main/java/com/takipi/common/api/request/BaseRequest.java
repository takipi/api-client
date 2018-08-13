package com.takipi.common.api.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.intf.ApiRequest;

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
}
