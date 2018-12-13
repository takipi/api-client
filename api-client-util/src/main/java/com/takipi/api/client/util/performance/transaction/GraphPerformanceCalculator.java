package com.takipi.api.client.util.performance.transaction;

import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.data.transaction.TransactionGraph.GraphPoint;
import com.takipi.api.client.util.performance.PerformanceState;
import com.takipi.api.client.util.performance.compare.PerformanceCalculator;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;

public class GraphPerformanceCalculator implements PerformanceCalculator<TransactionGraph>
{
	private final long activeInvocationsThreshold;
	private final double overAvgPercentage;
	private final double stdDevFactor;
	
	private GraphPerformanceCalculator(long activeInvocationsThreshold, double overAvgPercentage, double stdDevFactor) {
		this.activeInvocationsThreshold = activeInvocationsThreshold;
		this.overAvgPercentage = overAvgPercentage;
		this.stdDevFactor = stdDevFactor;
	}
	
	@Override
	public PerformanceState calc(TransactionGraph active, TransactionGraph baseline) {
		if ((active == null) || (CollectionUtil.safeIsEmpty(active.points)) || (baseline == null) || (CollectionUtil.safeIsEmpty(baseline.points))) {
			return PerformanceState.NO_DATA;
		}
		
		double totalActiveInvocations = 0.0;
		
		for (GraphPoint p : active.points) {
			if (p.stats == null) {
				continue;
			}
			
			totalActiveInvocations += p.stats.invocations;
		}
		
		if (totalActiveInvocations < this.activeInvocationsThreshold) {
			return PerformanceState.NO_DATA;
		}
		
		double[] baselineInvocations = new double[baseline.points.size()];
		double[] baselineAvg = new double[baseline.points.size()];

		for (int i = 0; i < baseline.points.size(); i++)
		{
			GraphPoint p = baseline.points.get(i);
			
			if (p.stats == null) {
				continue;
			}
			
			baselineInvocations[i] = p.stats.invocations;
			baselineAvg[i] = p.stats.avg_time;
		}
		
		double weightedAvg = MathUtil.weightedAvg(baselineAvg, baselineInvocations);
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
		
		boolean slowing = (badPointsScore >= this.overAvgPercentage);
		
		return (slowing ? PerformanceState.SLOWING : PerformanceState.OK);
	}
	
	public static GraphPerformanceCalculator of(long activeInvocationsThreshold, double overAvgPercentage, double stdDevFactor)
	{
		
		return new GraphPerformanceCalculator(activeInvocationsThreshold, overAvgPercentage, stdDevFactor);
	}
}
