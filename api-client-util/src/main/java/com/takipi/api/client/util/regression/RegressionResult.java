package com.takipi.api.client.util.regression;

import com.takipi.api.client.result.event.EventResult;

public class RegressionResult {
	private final EventResult event;
	private final long baselineHits;
	private final long baselineInvocations;

	private RegressionResult(EventResult event, long baselineHits, long baselineInvocations)
	{
		this.event = event;
		this.baselineHits = baselineHits;
		this.baselineInvocations = baselineInvocations;
	}
	
	public EventResult getEvent() {
		return event;
	}

	public long getBaselineHits() {
		return baselineHits;
	}

	public long getBaselineInvocations() {
		return baselineInvocations;
	}

	public static RegressionResult of(EventResult event, long baselineHits, long baselineInvocations) {
		return new RegressionResult(event, baselineHits, baselineInvocations);
	}
}
