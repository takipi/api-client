package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.common.util.CollectionUtil;

public class HybridPerformanceCalculator extends BaseGraphPerformanceCalculator<Transaction> {
	private HybridPerformanceCalculator(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double minDeltaThresholdPercentage, double overAvgSlowingPercentage,
			double overAvgCriticalPercentage, double stdDevFactor) {
		super(activeInvocationsThreshold, baselineInvocationsThreshold, minDeltaThreshold, minDeltaThresholdPercentage,
				overAvgSlowingPercentage, overAvgCriticalPercentage, stdDevFactor);
	}

	@Override
	public PerformanceScore calc(TransactionGraph active, Transaction baseline) {
		if ((active == null) || (CollectionUtil.safeIsEmpty(active.points)) || (baseline == null)
				|| (baseline.stats == null)) {
			return PerformanceScore.NO_DATA;
		}

		return doCalc(active, baseline.stats);
	}

	public static HybridPerformanceCalculator of(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double minDeltaThresholdPercentage, double overAvgSlowingPercentage,
			double overAvgCriticalPercentage, double stdDevFactor) {

		return new HybridPerformanceCalculator(activeInvocationsThreshold, baselineInvocationsThreshold,
				minDeltaThreshold, minDeltaThresholdPercentage, overAvgSlowingPercentage, overAvgCriticalPercentage,
				stdDevFactor);
	}
}
