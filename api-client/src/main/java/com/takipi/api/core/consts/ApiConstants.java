package com.takipi.api.core.consts;

import java.nio.charset.Charset;

public class ApiConstants {
	public static final String UTF8_ENCODING				= "UTF-8";
	public static final String UTF16LE_ENCODING				= "UTF-16LE";
	public static final String US_ASCII_ENCODING			= "US-ASCII";
	
	public static final String DEFAULT_CHARSET_ENCODING		= UTF8_ENCODING;
	public static final Charset DEFAULT_CHARSET				= Charset.forName(UTF8_ENCODING);
	
	public static final String CONTENT_TYPE_BINARY			= "application/octet-stream";
	public static final String CONTENT_TYPE_JSON			= "application/json";
	public static final String CONTENT_TYPE_TEXT			= "text/plain";
	public static final String CONTENT_TYPE_HTML			= "text/html";
	public static final String CONTENT_TYPE_XML				= "text/xml";
	
	public static final String HTTP_HEADER_USER_AGENT		= "User-Agent";
	
	public static final String HTTP_PROTOCOL				= "http";
	public static final String HTTPS_PROTOCOL				= "https";
	
	public static final String SERVICES_PREFIX				= "S";
	public static final String VIEWS_PREFIX					= "P";
	public static final String PACK_PREFIX					= "K";
}
