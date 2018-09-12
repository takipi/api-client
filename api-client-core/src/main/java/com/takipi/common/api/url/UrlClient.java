package com.takipi.common.api.url;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.util.Pair;

public abstract class UrlClient {
	protected static final Logger logger = LoggerFactory.getLogger(UrlClient.class);

	static final Response<String> BAD_RESPONSE = Response.of(HttpURLConnection.HTTP_INTERNAL_ERROR, null);

	private final String hostname;
	private final int connectTimeout;
	private final int readTimeout;

	protected UrlClient(String hostname, int connectTimeout, int readTimeout) {
		this.hostname = hostname;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
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

			return getResponse(targetUrl, connection);
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

			return getResponse(targetUrl, connection);
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

	public Response<String> post(String targetUrl, Pair<String, String> auth, byte[] data, String contentType) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(targetUrl);

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

			return getResponse(targetUrl, connection);
		} catch (Exception ex) {
			logger.error("Url client POST {} failed.", targetUrl, ex);

			return BAD_RESPONSE;
		} finally {
			closeQuietly(connection);
		}
	}

	public Response<String> delete(String targetUrl, Pair<String, String> auth, String contentType) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(targetUrl);

			connection = (HttpURLConnection) url.openConnection();

			if (auth != null) {
				connection.setRequestProperty(auth.getFirst(), auth.getSecond());
			}

			connection.setRequestProperty("Content-Type", contentType);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("DELETE");

			return getResponse(targetUrl, connection);
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
			isr = new InputStreamReader(is, StandardCharsets.UTF_8);

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
			} catch (Exception ignored) {
			}
		}
	}

	private void closeQuietly(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception ignored) {
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
			return new Response<>(responseCode, data);
		}
	}
}
