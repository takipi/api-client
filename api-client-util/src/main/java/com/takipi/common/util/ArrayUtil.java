package com.takipi.common.util;

import java.util.Collection;

public class ArrayUtil {
	
	public static String[] safeSplitArray(String value, String seperator, boolean removeWhitespace) {
		if ((value == null) || (value.isEmpty())) {
			return new String[0];
		}

		String[] result;
		
		if (removeWhitespace) {
			result = value.replaceAll("\\s", "").split(seperator);
		}  else {
			result = value.split(seperator);
		}
		
		return result;
	}
	
	public static boolean compare(Collection<String> a, Collection<String> b) {
		
		if (a == null) {
			return b == null;
		} 
		
		if (b == null) {
			return false;
		}
		
		if (a.size() != b.size()) {
			return false;
		}
		
		return a.containsAll(b);	
	}
}
