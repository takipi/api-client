package com.takipi.api.client.util.performance.calc;

public class PerformanceScore {
	public static final PerformanceScore NO_DATA = of(PerformanceState.NO_DATA, 0.0);

	public final PerformanceState state;
	public final double score;

	private PerformanceScore(PerformanceState state, double score) {
		this.state = state;
		this.score = score;
	}

	public static PerformanceScore of(PerformanceState state, double score) {
		return new PerformanceScore(state, score);
	}
}
