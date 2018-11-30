package com.takipi.api.core.url;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.url.UrlClientObserver.Operation;
import com.takipi.common.util.Pair;

public abstract class UrlClient {

	protected static final Logger logger = LoggerFactory.getLogger(UrlClient.class);

	static final Response<String> BAD_RESPONSE = Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);

	private final String hostname;
	private final int connectTimeout;
	private final int readTimeout;
	private final Object observerLock;
	private volatile List<UrlClientObserver> observers;

	protected UrlClient(String hostname, int connectTimeout, int readTimeout) {
		this.hostname = hostname;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.observerLock = new Object();
	}

	public String getHostname() {
		return hostname;
	}

	public Response<String> get(String targetUrl, Pair<String, String> auth, String contentType, String... params) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(appendQueryParams(targetUrl, params));

			connection = (HttpURLConnection) url.openConnection();

			if (auth != null) {
				connection.setRequestProperty(auth.getFirst(), auth.getSecond());
			}

			connection.setRequestProperty("Content-Type", contentType);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("GET");

			long t1 = System.currentTimeMillis();
			Response<String> result = getResponse(targetUrl, connection);
			long t2 = System.currentTimeMillis();

			observe(Operation.GET, url.toString(), result.data, t2 - t1);

			return result;
		} catch (Exception ex) {
			logger.error("Url client GET {} failed.", targetUrl, ex);

			return BAD_RESPONSE;
		} finally {
			closeQuietly(connection);
		}
	}

	public Response<String> put(String targetUrl, Pair<String, String> auth, byte[] data, String contentType,
			String... params) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(appendQueryParams(targetUrl, params));

			connection = (HttpURLConnection) url.openConnection();

			if (auth != null) {
				connection.setRequestProperty(auth.getFirst(), auth.getSecond());
			}

			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", ApiConstants.CONTENT_TYPE_JSON);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");

			if ((data != null) && (data.length > 0)) {
				OutputStream out = connection.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
			}

			long t1 = System.currentTimeMillis();
			Response<String> result = getResponse(targetUrl, connection);
			long t2 = System.currentTimeMillis();

			observe(Operation.PUT, url.toString(), result.data, t2 - t1);

			return result;

		} catch (Exception ex) {
			logger.error("Url client POST {} failed.", targetUrl, ex);

			return BAD_RESPONSE;
		} finally {
			closeQuietly(connection);
		}
	}

	private String appendQueryParams(String url, String[] params) {
		if ((params == null) || (params.length == 0)) {
			return url;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(url);

		boolean first = true;

		for (String param : params) {
			if (Strings.isNullOrEmpty(param)) {
				continue;
			}

			char prefix = (first ? '?' : '&');

			sb.append(prefix);
			sb.append(param);

			first = false;
		}

		return sb.toString();
	}

	public Response<String> post(String targetUrl, Pair<String, String> auth, byte[] data, String contentType,
			String... params) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(appendQueryParams(targetUrl, params));

			connection = (HttpURLConnection) url.openConnection();

			if (auth != null) {
				connection.setRequestProperty(auth.getFirst(), auth.getSecond());
			}

			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", ApiConstants.CONTENT_TYPE_JSON);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");

			if ((data != null) && (data.length > 0)) {
				OutputStream out = connection.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
			}

			long t1 = System.currentTimeMillis();
			Response<String> result = getResponse(targetUrl, connection);
			long t2 = System.currentTimeMillis();

			observe(Operation.POST, url.toString(), result.data, t2 - t1);

			return result;

		} catch (Exception ex) {
			logger.error("Url client POST {} failed.", targetUrl, ex);

			return BAD_RESPONSE;
		} finally {
			closeQuietly(connection);
		}
	}

	public Response<String> delete(String targetUrl, Pair<String, String> auth, String contentType, String... params) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(appendQueryParams(targetUrl, params));

			connection = (HttpURLConnection) url.openConnection();

			if (auth != null) {
				connection.setRequestProperty(auth.getFirst(), auth.getSecond());
			}

			connection.setRequestProperty("Content-Type", contentType);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("DELETE");

			long t1 = System.currentTimeMillis();
			Response<String> result = getResponse(targetUrl, connection);
			long t2 = System.currentTimeMillis();

			observe(Operation.DELETE, url.toString(), result.data, t2 - t1);

			return result;
		} catch (Exception ex) {
			logger.error("Url client DELETE {} failed.", targetUrl, ex);

			return BAD_RESPONSE;
		} finally {
			closeQuietly(connection);
		}
	}

	private Response<String> getResponse(String targetUrl, HttpURLConnection connection) {
		try {
			int responseCode = connection.getResponseCode();

			String responseData;

			if (!isBadResponse(responseCode)) {
				responseData = parseInputStream(connection.getInputStream());
			} else {
				responseData = parseInputStream(connection.getErrorStream());

				String errorMessage = responseData;

				if (Strings.isNullOrEmpty(errorMessage)) {
					errorMessage = "No error message received";
				}

				String prettyErrorMessage = prettifyErrorMessage(errorMessage, responseCode, 2000);

				logger.error("url client returns with bad response. url={}, code={}, error={}.", targetUrl,
						responseCode, prettyErrorMessage);
			}

			return Response.of(responseCode, responseData);
		} catch (Exception ex) {
			logger.error("Error parsing response from {}.", targetUrl, ex);

			return BAD_RESPONSE;
		}
	}

	public void addObserver(UrlClientObserver observer) {

		if (observer == null) {
			throw new IllegalArgumentException("observer");
		}

		synchronized (observerLock) {
			List<UrlClientObserver> observers;

			if (this.observers != null) {
				observers = Lists.newArrayList(this.observers);
			} else {
				observers = Lists.newArrayList();
			}

			observers.add(observer);
			this.observers = observers;
		}
	}

	public void removeObserver(UrlClientObserver observer) {

		if (observer == null) {
			throw new IllegalArgumentException("observer");
		}

		synchronized (observerLock) {
			if ((observers != null) && (observers.contains(observer))) {
				List<UrlClientObserver> observers = Lists.newArrayList(this.observers);
				observers.remove(observer);
				this.observers = observers;
			}
		}
	}

	private void observe(Operation operation, String url, String response, long time) {

		List<UrlClientObserver> observers = this.observers;

		if (observers == null) {
			return;
		}

		for (UrlClientObserver observer : observers) {
			observer.observe(operation, url, response, time);
		}
	}

	private String prettifyErrorMessage(String errorMessage, int responseCode, int maxMessageLength) {
		if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
			return "N/A";
		}

		String squashedString = squashString(errorMessage);

		return ellipsize(squashedString, maxMessageLength);
	}

	private String squashString(String str) {
		return str.replaceAll("[\\n\\r\\s]+", " ");
	}

	private String ellipsize(String str, int targetLength) {
		return ellipsize(str, targetLength, 1.0);
	}

	private String ellipsize(String str, int targetLength, double location) {
		return ellipsize(str, targetLength, location, "...");
	}

	private String ellipsize(String str, int targetLength, double location, String placeholder) {
		if (str.length() <= targetLength) {
			return str;
		}

		int netLength = targetLength - placeholder.length();

		int leftLength = (int) Math.round(netLength * location);
		int rightLength = netLength - leftLength;

		String leftStr = str.substring(0, leftLength);
		String rightStr = str.substring(str.length() - rightLength);

		return (leftStr + placeholder + rightStr);
	}

	private String parseInputStream(InputStream is) {
		if (is == null) {
			return null;
		}

		InputStreamReader isr = null;

		try {
			isr = new InputStreamReader(is, ApiConstants.UTF8_ENCODING);

			return IOUtils.toString(isr);
		} catch (Exception ex) {
			return null;
		} finally {
			closeQuietly(isr);
		}
	}

	private void closeQuietly(HttpURLConnection connection) {
		if (connection != null) {
			try {
				connection.disconnect();
			} catch (Exception e) {
			}
		}
	}

	private void closeQuietly(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}

	static boolean isBadResponse(int responseCode) {
		return (responseCode >= HttpURLConnection.HTTP_MULT_CHOICE);
	}

	public static class Response<T> {
		public final int responseCode;
		public final T data;

		private Response(int responseCode, T data) {
			this.responseCode = responseCode;
			this.data = data;
		}

		public boolean isOK() {
			return !isBadResponse();
		}

		public boolean isBadResponse() {
			return UrlClient.isBadResponse(responseCode);
		}

		public static <T> Response<T> of(int responseCode, T data) {
			return new Response<T>(responseCode, data);
		}
	}
}
