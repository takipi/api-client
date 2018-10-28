package com.takipi.api.client.util.regression;

import com.takipi.api.client.result.event.EventResult;

public class RegressionResult {
	private EventResult event;
	private long baselineHits;
	private long baselinInvocations;

	public EventResult getEvent() {
		return event;
	}

	public long getBaselineHits() {
		return baselineHits;
	}

	public long getBaselineInvocations() {
		return baselinInvocations;
	}

	public static RegressionResult of(EventResult event, long baselineHits, long baselinInvocations) {
		RegressionResult result = new RegressionResult();
		result.event = event;
		result.baselineHits = baselineHits;
		result.baselinInvocations = baselinInvocations;

		return result;
	}
}
