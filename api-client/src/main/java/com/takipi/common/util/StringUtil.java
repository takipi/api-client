package com.takipi.common.util;

public class StringUtil {
	public static boolean isNullOrEmpty(String s) {
		return ((s == null) || (s.isEmpty()));
	}

	public static String nullToEmpty(String s) {
		return ((s == null) ? "" : s);
	}
}
