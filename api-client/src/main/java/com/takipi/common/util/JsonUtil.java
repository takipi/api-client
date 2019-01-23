package com.takipi.common.util;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtil {
	private final static String JSON_PREFIX = "{";
	private final static String JSON_SUFFIX = "}";

	public static String stringify(String s) {
		return "\"" + s + "\"";
	}

	public static String createSimpleJson(Iterable<String> arr) {
		return createSimpleJson(arr, false);
	}

	public static String createSimpleJson(Iterable<String> arr, boolean stringifyValues) {
		StringBuilder sb = new StringBuilder();

		sb.append("[");

		boolean isFirst = true;

		for (String entry : arr) {
			if (!isFirst) {
				sb.append(",");
			}

			if (stringifyValues) {
				sb.append(stringify(entry));
			} else {
				sb.append(entry);
			}

			isFirst = false;
		}

		sb.append("]\n");

		return sb.toString();
	}

	public static String createSimpleJson(Map<String, String> map) {
		return createSimpleJson(map, false);
	}

	public static String createSimpleJson(Map<String, String> map, boolean stringifyValues) {
		StringBuilder sb = new StringBuilder();

		sb.append("{");

		boolean isFirst = true;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (!isFirst) {
				sb.append(",");
			}

			sb.append("\n\t");

			sb.append("\"" + key + "\": ");

			if (stringifyValues) {
				sb.append(stringify(value));
			} else {
				sb.append(value);
			}

			isFirst = false;
		}

		sb.append("\n}\n");

		return sb.toString();
	}

	public static boolean isLegalJson(String jsonStr) {
		if ((!jsonStr.startsWith(JSON_PREFIX)) || (!jsonStr.endsWith(JSON_SUFFIX))) {
			return false;
		}

		try {
			Gson gson = new Gson();

			gson.fromJson(jsonStr, Object.class);

			return true;
		} catch (JsonSyntaxException e) {
			return false;
		}
	}
}
