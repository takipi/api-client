package com.takipi.api.client.util.performance.compare;

import com.takipi.api.client.util.performance.PerformanceState;

public interface PerformanceCalculator<T> {
	public PerformanceState calc(T active, T baseline);
}
