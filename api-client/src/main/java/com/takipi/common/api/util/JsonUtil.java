package com.takipi.common.api.util;

import java.util.Map;

public class JsonUtil {
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
}
