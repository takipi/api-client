package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.transaction.TransactionUtil;
import com.takipi.common.util.CollectionUtil;

public class GraphPerformanceCalculator extends BaseGraphPerformanceCalculator<TransactionGraph> {
	private GraphPerformanceCalculator(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double minDeltaThresholdPercentage, double overAvgSlowingPercentage,
			double overAvgCriticalPercentage, double stdDevFactor) {
		super(activeInvocationsThreshold, baselineInvocationsThreshold, minDeltaThreshold, minDeltaThresholdPercentage,
				overAvgSlowingPercentage, overAvgCriticalPercentage, stdDevFactor);
	}

	@Override
	public PerformanceScore calc(TransactionGraph active, TransactionGraph baseline) {
		if ((active == null) || (CollectionUtil.safeIsEmpty(active.points)) || (baseline == null)
				|| (CollectionUtil.safeIsEmpty(baseline.points))) {
			return PerformanceScore.NO_DATA;
		}

		Stats baselineStats = TransactionUtil.aggregateGraph(baseline);

		return doCalc(active, baselineStats);
	}

	public static GraphPerformanceCalculator of(long activeInvocationsThreshold, long baselineInvocationsThreshold,
			int minDeltaThreshold, double minDeltaThresholdPercentage, double overAvgSlowingPercentage,
			double overAvgCriticalPercentage, double stdDevFactor) {

		return new GraphPerformanceCalculator(activeInvocationsThreshold, baselineInvocationsThreshold,
				minDeltaThreshold, minDeltaThresholdPercentage, overAvgSlowingPercentage, overAvgCriticalPercentage,
				stdDevFactor);
	}
}
