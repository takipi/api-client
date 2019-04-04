package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.util.performance.calc.PerformanceCalculator;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.performance.calc.PerformanceState;

public class AvgTimePerformanceCalculator implements PerformanceCalculator<Transaction, Transaction> {
	private final long minActiveInvocations;
	private final long minBaselineInvocations;
	private final double slowThresholdPercentage;
	private final double criticalThresholdPercentage;

	private AvgTimePerformanceCalculator(long minActiveInvocations, long minBaselineInvocations,
			double slowThresholdPercentage, double criticalThresholdPercentage) {

		this.minActiveInvocations = minActiveInvocations;
		this.minBaselineInvocations = minBaselineInvocations;
		this.slowThresholdPercentage = slowThresholdPercentage;
		this.criticalThresholdPercentage = criticalThresholdPercentage;
	}

	@Override
	public PerformanceScore calc(Transaction active, Transaction baseline) {
		if ((active == null) || (active.stats == null) || (active.stats.avg_time <= 0) || (baseline == null)
				|| (baseline.stats == null) || (baseline.stats.avg_time <= 0)) {
			return PerformanceScore.NO_DATA;
		}

		Stats activeStats = active.stats;
		Stats baselineStats = baseline.stats;

		if ((activeStats.invocations < minActiveInvocations) || (baselineStats.invocations < minBaselineInvocations)) {
			return PerformanceScore.NO_DATA;
		}

		double rateIncrease = prettyPercentage(activeStats.avg_time, baselineStats.avg_time);

		if (activeStats.avg_time >= (baselineStats.avg_time * criticalThresholdPercentage)) {
			return PerformanceScore.of(PerformanceState.CRITICAL, rateIncrease);
		}

		if (activeStats.avg_time >= (baselineStats.avg_time * slowThresholdPercentage)) {
			return PerformanceScore.of(PerformanceState.SLOWING, rateIncrease);
		}

		return PerformanceScore.of(PerformanceState.OK, rateIncrease);
	}

	private static double prettyPercentage(double numerator, double denominator) {
		return ((numerator / denominator) - 1) * 100.0;
	}

	public static AvgTimePerformanceCalculator of(long minActiveInvocations, long minBaselineInvocations,
			double slowThresholdPercentage, double criticalThresholdPercentage) {

		return new AvgTimePerformanceCalculator(minActiveInvocations, minBaselineInvocations, slowThresholdPercentage,
				criticalThresholdPercentage);
	}
}
