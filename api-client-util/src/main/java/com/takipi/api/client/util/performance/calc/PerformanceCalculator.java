package com.takipi.api.client.util.performance.calc;

public interface PerformanceCalculator<T, S> {
	public PerformanceScore calc(T active, S baseline);
}
