package com.takipi.common.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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

	public static String escapeText(String input) {
		StringBuilder builder = new StringBuilder(input.length());
		CharacterIterator iter = new StringCharacterIterator(input);

		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
			switch (c) {
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			case '\\':
				builder.append("\\\\");
				break;
			case '"':
				builder.append("\\\"");
				break;
			default:
				// Check for other control characters
				//
				if (c >= 0x0000 && c <= 0x001F) {
					appendEscapedUnicode(builder, c);
				} else if (Character.isHighSurrogate(c)) {
					// Encode the surrogate pair using 2 six-character sequence (\\uXXXX\\uXXXX)
					//
					appendEscapedUnicode(builder, c);
					c = iter.next();

					if (c == CharacterIterator.DONE)
						throw new IllegalArgumentException(
								"invalid unicode string: unexpected high surrogate pair value without corresponding low value.");

					appendEscapedUnicode(builder, c);
				} else {
					// Anything else can be printed as-is
					//
					builder.append(c);
				}
				break;
			}
		}
		return builder.toString();
	}

	private static void appendEscapedUnicode(StringBuilder builder, char ch) {
		String prefix = "\\u";

		if (ch < 0x10) {
			prefix = "\\u000";
		} else if (ch < 0x100) {
			prefix = "\\u00";
		} else if (ch < 0x1000) {
			prefix = "\\u0";
		}

		builder.append(prefix).append(Integer.toHexString(ch));
	}
}
