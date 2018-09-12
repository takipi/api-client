package com.takipi.common.api.util;

import java.io.Serializable;

public class Pair<T, S> implements Serializable {
	private static final long serialVersionUID = -1362336430156481925L;

	private final T first;
	private final S second;

	public static <T, S> Pair<T, S> of(T first, S second) {
		return new Pair<>(first, second);
	}

	public static <T, S> Pair<T, S> nulls() {
		return new Pair<>(null, null);
	}

	@SuppressWarnings("unchecked")
	public static <T, S> Class<Pair<T, S>> clazz() {
		return (Class<Pair<T, S>>) nulls().getClass();
	}

	private Pair(T first, S second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("<").append(first).append(", ").append(second).append(">").toString();
	}

	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof Pair)) {
			return false;
		}

		Pair<?, ?> other = (Pair<?, ?>) o;

		return ((nullEquals(this.first, other.first)) && (nullEquals(this.second, other.second)));
	}

	@Override
	public int hashCode() {
		int result = 0;

		if (first != null) {
			result ^= first.hashCode();
		}

		if (second != null) {
			result ^= second.hashCode();
		}

		return result;
	}

	private static boolean nullEquals(Object o1, Object o2) {
		if (o1 != null) {
			return o1.equals(o2);
		} else {
			return (o2 == null);
		}
	}
}