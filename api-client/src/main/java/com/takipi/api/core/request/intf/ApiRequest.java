package com.takipi.api.core.request.intf;

import java.io.UnsupportedEncodingException;

public interface ApiRequest {
	public String urlPath();

	public String[] queryParams() throws UnsupportedEncodingException;

	public String contentType();
}
