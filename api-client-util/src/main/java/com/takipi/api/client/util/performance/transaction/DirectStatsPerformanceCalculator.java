package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.common.util.CollectionUtil;

public class DirectStatsPerformanceCalculator extends BaseGraphPerformanceCalculator<Stats> {
	private DirectStatsPerformanceCalculator(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double minDeltaThresholdPercentage, double overAvgSlowingPercentage,
			double overAvgCriticalPercentage, double stdDevFactor, long maxAvgTimeThreshold) {
		super(activeInvocationsThreshold, baselineInvocationsThreshold, minDeltaThreshold, minDeltaThresholdPercentage,
				overAvgSlowingPercentage, overAvgCriticalPercentage, stdDevFactor, maxAvgTimeThreshold);
	}

	@Override
	public PerformanceScore calc(TransactionGraph active, Stats baselineStats) {
		if ((active == null) || (CollectionUtil.safeIsEmpty(active.points)) || (baselineStats == null)) {
			return PerformanceScore.NO_DATA;
		}

		return doCalc(active, baselineStats);
	}

	public static DirectStatsPerformanceCalculator of(long activeInvocationsThreshold,
			long baselineInvocationsThreshold, int minDeltaThreshold, double minDeltaThresholdPercentage,
			double overAvgSlowingPercentage, double overAvgCriticalPercentage, double stdDevFactor
			, long maxAvgTimeThreshold) {

		return new DirectStatsPerformanceCalculator(activeInvocationsThreshold, baselineInvocationsThreshold,
				minDeltaThreshold, minDeltaThresholdPercentage, overAvgSlowingPercentage, overAvgCriticalPercentage,
				stdDevFactor, maxAvgTimeThreshold);
	}
}
