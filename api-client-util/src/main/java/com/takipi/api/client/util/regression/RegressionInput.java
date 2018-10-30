package com.takipi.api.client.util.regression;

import java.util.Collection;

import org.joda.time.DateTime;

public class RegressionInput {
	public String serviceId;
	public String viewId;
	public int activeTimespan;
	public int baselineTimespan;
	public int minVolumeThreshold;
	public double minErrorRateThreshold;
	public double regressionDelta;
	public double criticalRegressionDelta;
	public boolean applySeasonality;
	public DateTime startTime;
	public Collection<String> criticalExceptionTypes;
	public Collection<String> applictations;
	public Collection<String> deployments;
	public Collection<String> servers;

	@Override
	public String toString() {
		return "Environment: " + serviceId + " View: " + viewId;
	}

	public void validate() {
		if ((serviceId == null) || (serviceId.isEmpty())) {
			throw new IllegalStateException("Missing Environment Id");
		}

		if ((viewId == null) || (viewId.isEmpty())) {
			throw new IllegalStateException("Missing View Id");
		}

		if (activeTimespan <= 0) {
			throw new IllegalStateException("Missing Active timespan");
		}

		if (baselineTimespan <= 0) {
			throw new IllegalStateException("Missing Active timespan");
		}

		if (regressionDelta <= 0) {
			throw new IllegalStateException("Missing Regression Delta");
		}

		if (criticalRegressionDelta <= 0) {
			throw new IllegalStateException("Missing Critical Regression Delta");
		}
	}
}
