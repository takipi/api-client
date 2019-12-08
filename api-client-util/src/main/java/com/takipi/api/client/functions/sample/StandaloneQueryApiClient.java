package com.takipi.api.client.functions.sample;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.BaseApiClient;
import com.takipi.api.client.observe.Observer;
import com.takipi.api.core.request.intf.ApiRequest;
import com.takipi.common.util.Pair;

public class StandaloneQueryApiClient extends BaseApiClient  implements ApiClient {
	private static final String OO_AS_INFLUX_PATH = "/oo-as-influx";

	StandaloneQueryApiClient(String hostname, Pair<String, String> auth, int connectTimeout, int readTimeout,
			LogLevel defaultLogLevel, Map<Integer, LogLevel> responseLogLevels, Collection<Observer> observers) {
		super(hostname, auth, connectTimeout, readTimeout, defaultLogLevel, responseLogLevels, observers);
	}

	@Override
	protected String baseApiPath() {
		return getHostname();
	}

	@Override
	protected String buildTargetUrl(ApiRequest apiRequest) throws UnsupportedEncodingException {
		return super.buildTargetUrl(apiRequest).replace(OO_AS_INFLUX_PATH, "");
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof StandaloneQueryApiClient)) {
			return false;
		}

		return super.equals(obj);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends BaseApiClient.Builder {
		Builder() {

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

		public StandaloneQueryApiClient build() {
			if (Strings.isNullOrEmpty(hostname)) {
				throw new IllegalArgumentException("Missing hostname");
			}

			return new StandaloneQueryApiClient(hostname, getAuth(), connectTimeout, readTimeout, defaultLogLevel,
					ImmutableMap.copyOf(responseLogLevels), observers);
		}
	}

	@Override
	public int getApiVersion() {
		return 0;
	}
}
