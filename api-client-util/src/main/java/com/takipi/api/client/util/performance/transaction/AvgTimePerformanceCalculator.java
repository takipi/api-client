package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.util.performance.PerformanceState;
import com.takipi.api.client.util.performance.compare.PerformanceCalculator;

public class AvgTimePerformanceCalculator implements PerformanceCalculator<Transaction> {
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
	public PerformanceState calc(Transaction active, Transaction baseline) {
		if ((active == null) || (active.stats == null) || (baseline == null) || (baseline.stats == null)) {
			return PerformanceState.NO_DATA;
		}

		Stats activeStats = active.stats;
		Stats baselineStats = baseline.stats;
		
		if ((activeStats.invocations < minActiveInvocations) || (baselineStats.invocations < minBaselineInvocations)) {
			return PerformanceState.NO_DATA;
		}

		if (activeStats.avg_time >= (baselineStats.avg_time * criticalThresholdPercentage)) {
			return PerformanceState.CRITICAL;
		}

		if (activeStats.avg_time >= (baselineStats.avg_time * slowThresholdPercentage)) {
			return PerformanceState.SLOWING;
		}

		return PerformanceState.OK;
	}

	public static AvgTimePerformanceCalculator of(long minActiveInvocations, long minBaselineInvocations,
			double slowThresholdPercentage, double criticalThresholdPercentage) {

		return new AvgTimePerformanceCalculator(minActiveInvocations, minBaselineInvocations, slowThresholdPercentage,
				criticalThresholdPercentage);
	}
}
