package com.takipi.api.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.takipi.api.client.observe.Observer;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.common.util.Pair;
import com.takipi.common.util.StringUtil;

public class RemoteApiClient extends BaseApiClient implements ApiClient {

	private final int apiVersion;

	RemoteApiClient(String hostname, Pair<String, String> auth, int apiVersion, int connectTimeout, int readTimeout,
			LogLevel defaultLogLevel, Map<Integer, LogLevel> responseLogLevels, Collection<Observer> observers) {
		super(hostname, auth, connectTimeout, readTimeout, defaultLogLevel, responseLogLevels, observers);

		this.apiVersion = apiVersion;
	}

	@Override
	public int getApiVersion() {
		return apiVersion;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof RemoteApiClient)) {
			return false;
		}

		if (!super.equals(obj)) {
			return false;
		}

		RemoteApiClient other = (RemoteApiClient) obj;

		if (apiVersion != other.apiVersion) {
			return false;
		}

		return true;
	}

	public Response<String> testConnection() {
		return get(baseApiPath() + "/test", null, ApiConstants.CONTENT_TYPE_JSON);
	}

	public boolean validateConnection() {
		try {
			Response<String> response = testConnection();
			return ((response != null) && (!response.isBadResponse()));
		} catch (Exception e) {
			logger.error("Api url client validate connection to {} failed.", getHostname(), e);
			return false;
		}
	}

	@Override
	protected String baseApiPath() {
		return getHostname() + "/api/v" + apiVersion;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends BaseApiClient.Builder {
		private static final int API_VERSION = 1;

		private int apiVersion;

		Builder() {
			super();

			this.apiVersion = API_VERSION;
		}

		@Override
		public Builder setHostname(String hostname) {
			super.setHostname(hostname);

			return this;
		}

		@Override
		public Builder setUsername(String username) {
			super.setUsername(username);

			return this;
		}

		@Override
		public Builder setPassword(String password) {
			super.setPassword(password);

			return this;
		}

		@Override
		public Builder setApiKey(String apiKey) {
			super.setApiKey(apiKey);

			return this;
		}

		public Builder setApiVersion(int apiVersion) {
			this.apiVersion = apiVersion;

			return this;
		}

		@Override
		public Builder setConnectTimeout(int connectTimeout) {
			super.setConnectTimeout(connectTimeout);

			return this;
		}

		@Override
		public Builder setReadTimeout(int readTimeout) {
			super.setReadTimeout(readTimeout);

			return this;
		}

		@Override
		public Builder setDefaultLogLevel(LogLevel defaultLogLevel) {
			super.setDefaultLogLevel(defaultLogLevel);

			return this;
		}

		@Override
		public Builder setResponseLogLevel(int responseCode, LogLevel logLevel) {
			super.setResponseLogLevel(responseCode, logLevel);

			return this;
		}

		@Override
		public Builder addObserver(Observer observer) {
			super.addObserver(observer);

			return this;
		}

		public ApiClient build() {
			if (StringUtil.isNullOrEmpty(hostname)) {
				throw new IllegalArgumentException("Missing hostname");
			}

			return new RemoteApiClient(hostname, getAuth(), apiVersion, connectTimeout, readTimeout, defaultLogLevel,
					new HashMap<>(responseLogLevels), observers);
		}
	}
}
