package com.takipi.api.client.util.regression;

public class SeasonlityResult {
	public final long majorSpike;
	public final long minorSpikes;
	public final int makorSpikeIndex;

	public SeasonlityResult(long majorSpike, long minorSpikes, int makorSpikeIndex) {
		this.majorSpike = majorSpike;
		this.minorSpikes = minorSpikes;
		this.makorSpikeIndex = makorSpikeIndex;
	}
}
