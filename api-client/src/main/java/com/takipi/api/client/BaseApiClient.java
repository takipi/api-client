package com.takipi.api.client;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.takipi.api.client.observe.Observer;
import com.takipi.api.client.observe.Observer.Operation;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiDeleteRequest;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.api.core.request.intf.ApiPostBytesRequest;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.api.core.request.intf.ApiPutRequest;
import com.takipi.api.core.request.intf.ApiRequest;
import com.takipi.api.core.result.intf.ApiResult;
import com.takipi.api.core.url.UrlClient;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public abstract class BaseApiClient extends UrlClient {

	private static final Gson GSON = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	private final Pair<String, String> auth;

	private final Object observerLock;
	private volatile List<Observer> observers;

	BaseApiClient(String hostname, Pair<String, String> auth, int connectTimeout, int readTimeout,
			LogLevel defaultLogLevel, Map<Integer, LogLevel> responseLogLevels, Collection<Observer> observers) {
		super(hostname, connectTimeout, readTimeout, defaultLogLevel, responseLogLevels);

		this.auth = auth;

		this.observerLock = new Object();

		if (!CollectionUtil.safeIsEmpty(observers)) {
			this.observers = Lists.newArrayList(observers);
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof BaseApiClient)) {
			return false;
		}

		BaseApiClient other = (BaseApiClient) obj;

		if (!Objects.equal(getHostname(), other.getHostname())) {
			return false;
		}

		if (!Objects.equal(auth, other.auth)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return getHostname().hashCode() ^ auth.hashCode();
	}

	protected abstract String baseApiPath();

	private String buildTargetUrl(ApiRequest apiRequest) {
		return baseApiPath() + apiRequest.urlPath();
	}

	@Override
	protected HttpURLConnection getConnection(String targetUrl, String contentType, String[] params) throws Exception {
		HttpURLConnection connection = super.getConnection(targetUrl, contentType, params);

		if (connection == null) {
			return null;
		}

		if (auth != null) {
			connection.setRequestProperty(auth.getFirst(), auth.getSecond());
		}

		return connection;
	}

	public <T extends ApiResult> Response<T> get(ApiGetRequest<T> request) {
		try {
			long t1 = System.currentTimeMillis();
			Response<String> response = get(buildTargetUrl(request), request.contentType(), request.queryParams());
			long t2 = System.currentTimeMillis();

			observe(Operation.GET, appendQueryParams(request.urlPath(), request.queryParams()), null, response.data,
					response.responseCode, t2 - t1);

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client GET {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> put(ApiPutRequest<T> request) {
		try {
			long t1 = System.currentTimeMillis();
			Response<String> response = put(buildTargetUrl(request), request.putData(), request.contentType(),
					request.queryParams());
			long t2 = System.currentTimeMillis();

			observe(Operation.PUT, appendQueryParams(request.urlPath(), request.queryParams()),
					prettyBytes(request.putData()), response.data, response.responseCode, t2 - t1);

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client PUT {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> post(ApiPostRequest<T> request) {
		try {
			String postData = request.postData();
			byte[] data = (Strings.isNullOrEmpty(postData) ? null : postData.getBytes(ApiConstants.UTF8_ENCODING));

			long t1 = System.currentTimeMillis();
			Response<String> response = post(buildTargetUrl(request), data, request.contentType(),
					request.queryParams());
			long t2 = System.currentTimeMillis();

			observe(Operation.POST, appendQueryParams(request.urlPath(), request.queryParams()), postData,
					response.data, response.responseCode, t2 - t1);

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client POST {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> post(ApiPostBytesRequest<T> request) {
		try {
			long t1 = System.currentTimeMillis();
			Response<String> response = post(buildTargetUrl(request), request.postData(), request.contentType(),
					request.queryParams());
			long t2 = System.currentTimeMillis();

			observe(Operation.POST, appendQueryParams(request.urlPath(), request.queryParams()),
					prettyBytes(request.postData()), response.data, response.responseCode, t2 - t1);

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client POST {} failed.", request.getClass().getName(), e);
			return Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
		}
	}

	public <T extends ApiResult> Response<T> delete(ApiDeleteRequest<T> request) {
		try {
			String postData = request.postData();
			byte[] data = (Strings.isNullOrEmpty(postData) ? null : postData.getBytes(ApiConstants.UTF8_ENCODING));

			long t1 = System.currentTimeMillis();
			Response<String> response = delete(buildTargetUrl(request), data, request.contentType(),
					request.queryParams());
			long t2 = System.currentTimeMillis();

			observe(Operation.DELETE, appendQueryParams(request.urlPath(), request.queryParams()), null, response.data,
					response.responseCode, t2 - t1);

			return getApiResponse(response, request.resultClass());
		} catch (Exception e) {
			logger.error("Api url client DELETE {} failed.", request.getClass().getName(), e);
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

	public void addObserver(Observer observer) {

		if (observer == null) {
			throw new IllegalArgumentException("observer");
		}

		synchronized (observerLock) {
			List<Observer> observers;

			if (this.observers != null) {
				observers = Lists.newArrayList(this.observers);
			} else {
				observers = Lists.newArrayList();
			}

			observers.add(observer);
			this.observers = observers;
		}
	}

	public void removeObserver(Observer observer) {

		if (observer == null) {
			throw new IllegalArgumentException("observer");
		}

		synchronized (observerLock) {
			if ((observers != null) && (observers.contains(observer))) {
				List<Observer> observers = Lists.newArrayList(this.observers);
				observers.remove(observer);
				this.observers = observers;
			}
		}
	}

	private void observe(Operation operation, String url, String request, String response, int responseCode,
			long time) {

		List<Observer> observers = this.observers;

		if (observers == null) {
			return;
		}

		for (Observer observer : observers) {
			observer.observe(operation, url, request, response, responseCode, time);
		}
	}

	private String prettyBytes(byte[] bytes) {
		StringBuilder builder = new StringBuilder();

		builder.append("Binary data (");
		builder.append((bytes != null) ? bytes.length : 0);
		builder.append(")");

		return builder.toString();
	}

	public static class Builder {
		private static final int CONNECT_TIMEOUT = 15000;
		private static final int READ_TIMEOUT = 60000;

		protected String hostname;
		protected String username;
		protected String password;
		protected String apiKey;
		protected int connectTimeout;
		protected int readTimeout;
		protected LogLevel defaultLogLevel;
		protected Map<Integer, LogLevel> responseLogLevels;
		protected Collection<Observer> observers;

		Builder() {
			this.connectTimeout = CONNECT_TIMEOUT;
			this.readTimeout = READ_TIMEOUT;

			this.defaultLogLevel = LogLevel.ERROR;
			this.responseLogLevels = Maps.newHashMap();
			this.observers = Sets.newHashSet();
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

		public Builder setConnectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;

			return this;
		}

		public Builder setReadTimeout(int readTimeout) {
			this.readTimeout = readTimeout;

			return this;
		}

		public Builder setDefaultLogLevel(LogLevel defaultLogLevel) {
			this.defaultLogLevel = defaultLogLevel;

			return this;
		}

		public Builder setResponseLogLevel(int responseCode, LogLevel logLevel) {
			this.responseLogLevels.put(responseCode, logLevel);

			return this;
		}

		public Builder addObserver(Observer observer) {
			this.observers.add(observer);

			return this;
		}

		protected Pair<String, String> getAuth() {
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
	}
}
