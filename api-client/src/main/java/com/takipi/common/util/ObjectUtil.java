package com.takipi.common.util;

public class ObjectUtil {
	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
}
