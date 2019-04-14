package com.takipi.common.util;

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

	public static <T> boolean equalCollections(Collection<T> a, Collection<T> b) {
		if (a == null) {
			return (b == null);
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
