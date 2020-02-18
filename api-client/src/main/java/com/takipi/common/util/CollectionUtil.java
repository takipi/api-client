package com.takipi.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionUtil {
	public static <T> boolean safeIsEmpty(Collection<T> collection) {
		return ((collection == null) || (collection.isEmpty()));
	}

	public static <K, V> boolean safeIsEmpty(Map<K, V> map) {
		return ((map == null) || (map.isEmpty()));
	}

	public static <T> int safeSize(Collection<T> collection) {
		return (safeIsEmpty(collection) ? 0 : collection.size());
	}

	public static <K, V> int safeSize(Map<K, V> map) {
		return (safeIsEmpty(map) ? 0 : map.size());
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

	public static <K, V> Map<K, V> mapOf(K k1, V v1) {
		Map<K, V> map = new HashMap<>();

		map.put(k1, v1);

		return map;
	}

	public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
		Map<K, V> map = new HashMap<>();

		map.put(k1, v1);
		map.put(k2, v2);

		return map;
	}

	public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = new HashMap<>();

		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);

		return map;
	}

	public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		Map<K, V> map = new HashMap<>();

		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		map.put(k4, v4);

		return map;
	}

	public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		Map<K, V> map = new HashMap<>();

		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		map.put(k4, v4);
		map.put(k5, v5);

		return map;
	}
}
