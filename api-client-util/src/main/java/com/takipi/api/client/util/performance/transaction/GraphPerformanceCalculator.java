package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.data.transaction.TransactionGraph.GraphPoint;
import com.takipi.api.client.util.performance.calc.PerformanceCalculator;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.performance.calc.PerformanceState;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;

public class GraphPerformanceCalculator implements PerformanceCalculator<TransactionGraph> {
	private final long activeInvocationsThreshold;
	private final long baselineInvocationsThreshold;
	private final double overAvgSlowingPercentage;
	private final double overAvgCriticalPercentage;
	private final double stdDevFactor;

	private GraphPerformanceCalculator(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			double overAvgSlowingPercentage, double overAvgCriticalPercentage, double stdDevFactor) {

		this.activeInvocationsThreshold = activeInvocationsThreshold;
		this.baselineInvocationsThreshold = baselineInvocationsThreshold;
		this.overAvgSlowingPercentage = overAvgSlowingPercentage;
		this.overAvgCriticalPercentage = overAvgCriticalPercentage;
		this.stdDevFactor = stdDevFactor;
	}

	@Override
	public PerformanceScore calc(TransactionGraph active, TransactionGraph baseline) {
		if ((active == null) || (CollectionUtil.safeIsEmpty(active.points)) || (baseline == null)
				|| (CollectionUtil.safeIsEmpty(baseline.points))) {
			return PerformanceScore.NO_DATA;
		}

		double totalActiveInvocations = 0.0;

		for (GraphPoint p : active.points) {
			if (p.stats == null) {
				continue;
			}

			totalActiveInvocations += p.stats.invocations;
		}

		if (totalActiveInvocations < this.activeInvocationsThreshold) {
			return PerformanceScore.NO_DATA;
		}

		double[] baselineInvocations = new double[baseline.points.size()];

		double[] baselineAvg = new double[baseline.points.size()];

		for (int i = 0; i < baseline.points.size(); i++) {
			GraphPoint p = baseline.points.get(i);

			if (p.stats == null) {
				continue;
			}

			baselineInvocations[i] = p.stats.invocations;
			baselineAvg[i] = p.stats.avg_time;
		}

		double totalBaselineInvocations = MathUtil.sum(baselineInvocations);

		if (totalBaselineInvocations < this.baselineInvocationsThreshold) {
			return PerformanceScore.NO_DATA;
		}

		double weightedAvg = MathUtil.weightedAvg(baselineAvg, baselineInvocations);

		if (weightedAvg <= 0) {
			return PerformanceScore.NO_DATA;
		}

		double weightedStdDev = MathUtil.wightedStdDev(baselineAvg, baselineInvocations);

		double badPointsScore = 0.0;
		double threshold = weightedAvg + (weightedStdDev * this.stdDevFactor);

		for (GraphPoint p : active.points) {
			if (p.stats == null) {
				continue;
			}

			if (p.stats.avg_time < threshold) {
				continue;
			}

			double pointScore = p.stats.invocations / totalActiveInvocations;

			badPointsScore += pointScore;
		}

		double slowingPercentage = badPointsScore * 100;

		if (badPointsScore >= this.overAvgCriticalPercentage) {
			return PerformanceScore.of(PerformanceState.CRITICAL, slowingPercentage);
		}

		if (badPointsScore >= this.overAvgSlowingPercentage) {
			return PerformanceScore.of(PerformanceState.SLOWING, slowingPercentage);
		}

		return PerformanceScore.of(PerformanceState.OK, slowingPercentage);
	}

	public static GraphPerformanceCalculator of(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			double overAvgSlowingPercentage, double overAvgCriticalPercentage, double stdDevFactor) {

		return new GraphPerformanceCalculator(activeInvocationsThreshold, baselineInvocationsThreshold,
				overAvgSlowingPercentage, overAvgCriticalPercentage, stdDevFactor);
	}
}
