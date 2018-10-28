package com.takipi.api.client.util.regression;

public class RegressionStats {
	public final long hits;
	public final long invocations;
	public final double rate;

	public final double hitsAvg;
	public final double invocationsAvg;
	public final double rateAvg;

	public final double hitsAvgStdDev;
	public final double invocationsAvgStdDev;
	public final double rateAvgStdDev;

	private RegressionStats(long hits, long invocations, double rate, double hitsAvg, double invocationsAvg,
			double rateAvg, double hitsAvgStdDev, double invocationsAvgStdDev, double rateAvgStdDev) {
		this.hits = hits;
		this.invocations = invocations;
		this.rate = rate;

		this.hitsAvg = hitsAvg;
		this.invocationsAvg = invocationsAvg;
		this.rateAvg = rateAvg;

		this.hitsAvgStdDev = hitsAvgStdDev;
		this.invocationsAvgStdDev = invocationsAvgStdDev;
		this.rateAvgStdDev = rateAvgStdDev;
	}

	public static class SeasonlityResult {
		public final long largerVolumePeriod;
		public final long halfVolumePeriods;
		public final int largerVolumePriodIndex;

		private SeasonlityResult(long largerVolumePeriod, long halfVolumePeriods, int largerVolumePriodIndex) {
			this.largerVolumePeriod = largerVolumePeriod;
			this.halfVolumePeriods = halfVolumePeriods;
			this.largerVolumePriodIndex = largerVolumePriodIndex;
		}

		static SeasonlityResult create(long largerVolumePeriod, long halfVolumePeriods,
				int largerVolumePriodIndex) {
			return new SeasonlityResult(largerVolumePeriod, halfVolumePeriods, largerVolumePriodIndex);
		}
	}

	static RegressionStats of(long hits, long invocations, double rate, double hitsAvg, double invocationsAvg,
			double rateAvg, double hitsAvgStdDev, double invocationsAvgStdDev, double rateAvgStdDev) {
		return new RegressionStats(hits, invocations, rate, hitsAvg, invocationsAvg, rateAvg, hitsAvgStdDev,
				invocationsAvgStdDev, rateAvgStdDev);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("hits = ");
		result.append(hits);

		result.append(", invocations = ");
		result.append(invocations);

		result.append(", rate = ");
		result.append(rate);

		result.append(", hitsAvg = ");
		result.append(hitsAvg);

		result.append(", invocationsAvg = ");
		result.append(invocationsAvg);

		result.append(", rateAvg = ");
		result.append(rateAvg);

		result.append(", hitsAvgStdDev = ");
		result.append(hitsAvgStdDev);

		result.append(", invocationsAvgStdDev = ");
		result.append(invocationsAvgStdDev);

		result.append(", rateAvgStdDev = ");
		result.append(rateAvgStdDev);

		return result.toString();
	}
}