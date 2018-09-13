package com.takipi.common.api;

import java.net.HttpURLConnection;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Strings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.request.intf.ApiPutRequest;
import com.takipi.common.api.request.intf.ApiRequest;
import com.takipi.common.api.result.intf.ApiResult;
import com.takipi.common.api.url.UrlClient;
import com.takipi.common.api.util.Pair;

public class ApiClient extends UrlClient {
	private static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	private final Pair<String, String> auth;
	private final int apiVersion;

	ApiClient(String hostname, Pair<String, String> auth, int apiVersion, int connectTimeout, int readTimeout) {
		super(hostname, connectTimeout, readTimeout);

		this.auth = auth;
		this.apiVersion = apiVersion;
	}

	public boolean validateConnection() {
		try {
			Response<String> response = get(baseApiPath() + "/test", null, ApiConstants.CONTENT_TYPE_JSON);

			return ((response != null) && (!response.isBadResponse()));
		} catch (Exception e) {
			logger.error("Api url client validate connection to {} failed.", getHostname(), e);
			return false;
		}
	}

	private String baseApiPath() {
		return getHostname() + "/api/v" + apiVersion;
	}

	private String buildTargetUrl(ApiRequest apiRequest) {
		return baseApiPath() + apiRequest.urlPath();
	}

	public <T extends ApiResult> Response<T> get(ApiGetRequest<T> request) {
		try {
			Response<String> response = get(buildTargetUrl(request), auth, request.contentType(), request.getParams());

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client GET {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> put(ApiPutRequest<T> request) {
		try {
			Response<String> response = put(buildTargetUrl(request), auth, request.getData(), request.contentType(),
					request.getParams());

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client GET {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> post(ApiPostRequest<T> request) {
		try {
			Response<String> response = post(buildTargetUrl(request), auth, request.postData(), request.contentType());

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client POST {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	private <T extends ApiResult> Response<T> getApiResponse(Response<String> response, Class<T> clazz) {
		if ((response.isBadResponse()) || (Strings.isNullOrEmpty(response.data))) {
			return Response.of(response.responseCode, null);
		}

		T apiResult = GSON.fromJson(response.data, clazz);

		return Response.of(response.responseCode, apiResult);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private static final int API_VERSION = 1;
		private static final int CONNECT_TIMEOUT = 15000;
		private static final int READ_TIMEOUT = 60000;

		private String hostname;
		private String username;
		private String password;
		private String apiKey;
		private int apiVersion;
		private int connectTimeout;
		private int readTimeout;

		Builder() {
			this.apiVersion = API_VERSION;
			this.connectTimeout = CONNECT_TIMEOUT;
			this.readTimeout = READ_TIMEOUT;
		}

		public Builder setHostname(String hostname) {
			this.hostname = hostname;

			return this;
		}

		public Builder setUsername(String username) {
			this.username = username;

			return this;
		}

		public Builder setPassword(String password) {
			this.password = password;

			return this;
		}

		public Builder setApiKey(String apiKey) {
			this.apiKey = apiKey;

			return this;
		}

		public Builder setApiVersion(int apiVersion) {
			this.apiVersion = apiVersion;

			return this;
		}

		public Builder setConnectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;

			return this;
		}

		public Builder setReadTimeout(int readTimeout) {
			this.readTimeout = readTimeout;

			return this;
		}

		private Pair<String, String> getAuth() {
			if (!Strings.isNullOrEmpty(apiKey)) {
				return Pair.of("X-API-Key", apiKey);
			}

			if (Strings.isNullOrEmpty(username)) {
				throw new IllegalArgumentException("Missing username/api key");
			}

			if (Strings.isNullOrEmpty(password)) {
				throw new IllegalArgumentException("Missing password");
			}

			String userPassword = username + ":" + password;
			byte[] userPasswordBytes = userPassword.getBytes();

			Base64 base64 = new Base64();
			byte[] encodedData = base64.encode(userPasswordBytes);

			String auth = "Basic " + new String(encodedData);

			return Pair.of("Authorization", auth);
		}

		public ApiClient build() {
			if (Strings.isNullOrEmpty(hostname)) {
				throw new IllegalArgumentException("Missing hostname");
			}

			return new ApiClient(hostname, getAuth(), apiVersion, connectTimeout, readTimeout);
		}
	}
}
