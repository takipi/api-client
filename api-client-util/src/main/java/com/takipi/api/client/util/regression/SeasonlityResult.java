package com.takipi.api.client.util.regression;

public class SeasonlityResult {
	public final long largerVolumePeriod;
	public final long halfVolumePeriods;
	public final int largerVolumePriodIndex;

	private SeasonlityResult(long largerVolumePeriod, long halfVolumePeriods, int largerVolumePriodIndex) {
		this.largerVolumePeriod = largerVolumePeriod;
		this.halfVolumePeriods = halfVolumePeriods;
		this.largerVolumePriodIndex = largerVolumePriodIndex;
	}

	public static SeasonlityResult create(long largerVolumePeriod, long halfVolumePeriods, int largerVolumePriodIndex) {
		return new SeasonlityResult(largerVolumePeriod, halfVolumePeriods, largerVolumePriodIndex);
	}
}
