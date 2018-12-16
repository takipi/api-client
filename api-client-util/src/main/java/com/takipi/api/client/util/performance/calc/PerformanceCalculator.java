package com.takipi.api.client.util.performance.calc;

public interface PerformanceCalculator<T> {
	public PerformanceScore calc(T active, T baseline);
}
