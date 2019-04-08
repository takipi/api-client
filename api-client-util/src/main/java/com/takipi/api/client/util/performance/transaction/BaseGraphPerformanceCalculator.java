package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.data.transaction.TransactionGraph.GraphPoint;
import com.takipi.api.client.util.performance.calc.PerformanceCalculator;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.performance.calc.PerformanceState;
import com.takipi.api.client.util.transaction.TransactionUtil;

public abstract class BaseGraphPerformanceCalculator<S> implements PerformanceCalculator<TransactionGraph, S> {
	private final long activeInvocationsThreshold;
	private final long baselineInvocationsThreshold;
	private final double overAvgSlowingPercentage;
	private final double overAvgCriticalPercentage;
	private final int minDeltaThreshold;
	private final double stdDevFactor;

	protected BaseGraphPerformanceCalculator(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double overAvgSlowingPercentage, double overAvgCriticalPercentage,
			double stdDevFactor) {

		this.activeInvocationsThreshold = activeInvocationsThreshold;
		this.baselineInvocationsThreshold = baselineInvocationsThreshold;
		this.minDeltaThreshold = minDeltaThreshold;
		this.overAvgSlowingPercentage = overAvgSlowingPercentage;
		this.overAvgCriticalPercentage = overAvgCriticalPercentage;
		this.stdDevFactor = stdDevFactor;
	}

	protected PerformanceScore doCalc(TransactionGraph active, Stats baselineStats) {
		Stats activeStats = TransactionUtil.aggregateGraph(active);

		if ((activeStats.invocations < this.activeInvocationsThreshold)
				|| (baselineStats.invocations < this.baselineInvocationsThreshold) || (baselineStats.avg_time <= 0.0)) {
			return PerformanceScore.NO_DATA;
		}

		double badPointsScore = 0.0;
		double threshold = baselineStats.avg_time + (baselineStats.avg_time_std_deviation * this.stdDevFactor);

		for (GraphPoint p : active.points) {
			if (p.stats == null) {
				continue;
			}

			if (p.stats.avg_time < threshold) {
				continue;
			}

			double pointScore = p.stats.invocations / (double) activeStats.invocations;

			badPointsScore += pointScore;
		}

		double slowingPercentage = badPointsScore * 100;

		if (activeStats.avg_time - baselineStats.avg_time > minDeltaThreshold) {

			if (badPointsScore >= this.overAvgCriticalPercentage) {
				return PerformanceScore.of(PerformanceState.CRITICAL, slowingPercentage);
			}

			if (badPointsScore >= this.overAvgSlowingPercentage) {
				return PerformanceScore.of(PerformanceState.SLOWING, slowingPercentage);
			}
		}

		return PerformanceScore.of(PerformanceState.OK, slowingPercentage);
	}
}
