package com.takipi.common.api.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {
	public static <T> boolean safeIsEmpty(Collection<T> collection) {
		return ((collection == null) || (collection.isEmpty()));
	}

	public static <K, V> boolean safeIsEmpty(Map<K, V> map) {
		return ((map == null) || (map.isEmpty()));
	}

	public static <T> boolean safeContains(Collection<T> collection, T item) {
		return ((collection != null) && (collection.contains(item)));
	}

	public static <K, V> boolean safeContainsKey(Map<K, V> map, K key) {
		return ((map != null) && (map.containsKey(key)));
	}
}
