package com.takipi.api.client.util.regression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.joda.time.DateTime;

import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.common.util.CollectionUtil;

public class RegressionInput {

	public static class TypeThresholds {
		public int minVolumeThreshold;
		public double minErrorRateThreshold;
		public double regressionDelta;
		public double criticalRegressionDelta;
	}

	public String serviceId;
	public String viewId;

	public DateTime activeWindowStart;
	public int activeTimespan;
	public int baselineTimespan;
	public String baselineTime;

	public int minVolumeThreshold;
	public double minErrorRateThreshold;
	public double regressionDelta;
	public double criticalRegressionDelta;

	public boolean applySeasonality;
	public Collection<String> criticalExceptionTypes;

	public Map<String, TypeThresholds> typeThresholdsMap;

	public Collection<String> applictations;
	public Collection<String> deployments;
	public Collection<String> servers;

	public Collection<EventResult> events;
	public Graph baselineGraph;

	private static void appendCollection(StringBuilder builder, String name, Collection<String> value,
			boolean newline) {
		builder.append(name);
		builder.append(" = ");

		if ((value != null) && (value.size() > 0)) {
			builder.append(Arrays.toString(value.toArray()));
		} else {
			builder.append("");
		}

		if (newline) {
			builder.append("\n");
		}
	}

	private static void appendVariable(StringBuilder builder, String name, Object value) {
		builder.append(name);
		builder.append(" = ");
		builder.append(value);
		builder.append("\n");
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		appendVariable(result, "Environment ID", serviceId);
		appendVariable(result, "View ID", viewId);
		appendVariable(result, "Active Timespan", activeTimespan);
		appendVariable(result, "Baseline Timespan", baselineTimespan);
		appendVariable(result, "Min Volume Threshold", minVolumeThreshold);
		appendVariable(result, "Min Rate Threshold", minErrorRateThreshold);
		appendVariable(result, "Regression Delta", regressionDelta);
		appendVariable(result, "Critical Regression Delta", criticalRegressionDelta);
		appendVariable(result, "Apply Seasonality", applySeasonality);
		appendCollection(result, "Critical Exception Types", criticalExceptionTypes, true);
		appendCollection(result, "Deployments", deployments, true);
		appendCollection(result, "Applications", applictations, true);
		appendCollection(result, "Servers", servers, false);

		return result.toString();
	}

	public void validate() {

		if ((serviceId == null) || (serviceId.isEmpty())) {
			throw new IllegalStateException("Missing Environment Id");
		}

		if ((viewId == null) || (viewId.isEmpty())) {
			throw new IllegalStateException("Missing View Id");
		}

		if (activeTimespan < 0) {
			throw new IllegalStateException("Negative Active timespan");
		}

		if ((CollectionUtil.safeIsEmpty(deployments)) && (activeTimespan == 0)) {
			throw new IllegalStateException("Either active timespan or deployment name must be provided");
		}

		if (baselineTimespan <= 0) {
			throw new IllegalStateException("Missing Baseline timespan");
		}

		if (regressionDelta < 0) {
			throw new IllegalStateException("Negative Regression Delta");
		}

		if (criticalRegressionDelta < 0) {
			throw new IllegalStateException("Negative Critical Regression Delta");
		}
	}

	public double getEventMinThreshold(EventResult event) {

		if (typeThresholdsMap == null) {
			return minVolumeThreshold;
		}

		TypeThresholds typeThresholds = typeThresholdsMap.get(event.type);

		if (typeThresholds != null) {
			return typeThresholds.minVolumeThreshold;
		} else {
			return minVolumeThreshold;
		}
	}

	public double getEventMinErrorRateThreshold(EventResult event) {

		if (typeThresholdsMap == null) {
			return minErrorRateThreshold;
		}

		TypeThresholds typeThresholds = typeThresholdsMap.get(event.type);

		if (typeThresholds != null) {
			return typeThresholds.minErrorRateThreshold;
		} else {
			return minErrorRateThreshold;
		}
	}

	public double getEventRegressionDelta(EventResult event) {

		if (typeThresholdsMap == null) {
			return regressionDelta;
		}

		TypeThresholds typeThresholds = typeThresholdsMap.get(event.type);

		if (typeThresholds != null) {
			return typeThresholds.regressionDelta;
		} else {
			return regressionDelta;
		}
	}

	public double getEventCriticalRegressionDelta(EventResult event) {

		if (typeThresholdsMap == null) {
			return criticalRegressionDelta;
		}

		TypeThresholds typeThresholds = typeThresholdsMap.get(event.type);

		if (typeThresholds != null) {
			return typeThresholds.criticalRegressionDelta;
		} else {
			return criticalRegressionDelta;
		}
	}
}
