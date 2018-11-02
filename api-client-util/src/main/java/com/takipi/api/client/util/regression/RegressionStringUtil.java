package com.takipi.api.client.util.regression;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.ocpsoft.prettytime.PrettyTime;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.result.event.EventResult;

public class RegressionStringUtil {

	private static final String DEPLOYMENT_FORMAT = "%s first seen %s against a baseline of %s";
	private static final String TIME_WINDOW_FORMAT = "last %s against a baseline of %s";

	public static final String NEW_ISSUE = "New";
	public static final String SEVERE_NEW = "Severe New";
	public static final String REGRESSION = "Regression";
	public static final String SEVERE_REGRESSION = "Severe Regression";

	private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

	public static String getEventSummary(EventResult event) {
		String[] parts = event.error_location.class_name.split(Pattern.quote("."));

		String simpleClassName;

		if (parts.length > 0) {
			simpleClassName = parts[parts.length - 1];
		} else {
			simpleClassName = event.error_location.class_name;
		}

		return event.type + " in " + simpleClassName + "." + event.error_location.method_name;
	}

	public static String getEventRate(EventResult event) {
		if ((event.stats.invocations == 0) || (event.stats.hits == 0)) {
			return "1";
		}

		StringBuilder result = new StringBuilder();

		double rate = (double) event.stats.hits / (double) event.stats.invocations * 100;

		result.append(event.stats.hits);
		result.append("/");
		result.append(event.stats.invocations);
		result.append(" (");
		String fmt = decimalFormat.format(rate);

		if (fmt.startsWith(".")) {
			result.append("0");
		}

		result.append(fmt);
		result.append("%)");

		return result.toString();
	}

	public static String getRegressedEventRate(RegressionResult regressionResult) {
		return getRegressedEventRate(regressionResult.getEvent(), regressionResult.getBaselineHits(),
				regressionResult.getBaselineInvocations());
	}

	public static String getRegressedEventRate(EventResult event, long baselineHits, long baselineInvocations) {
		double rate = (double) baselineHits / (double) baselineInvocations * 100;

		StringBuilder result = new StringBuilder();
		result.append(getEventRate(event));
		result.append(" from ");
		result.append(decimalFormat.format(rate));
		result.append("%");

		return result.toString();
	}

	public static String getIntroducedBy(EventResult event) {
		return event.introduced_by;
	}

	public static String getRegressionName(RegressionInput input, DateTime activeWindowStart) {

		String result = null;

		if ((activeWindowStart != null) && (input.deployments != null) && (input.deployments.size() > 0)) {
			result = getRegressionDeploymentName(input, activeWindowStart);
		}

		if (result == null) {
			result = getRegressionTimeWindowName(input);
		}

		return result;
	}

	public static String getRegressionName(ApiClient apiClient, RegressionInput input) {

		String result = null;

		if ((input.deployments != null) && (input.deployments.size() > 0)) {
			result = getRegressionDeploymentName(apiClient, input);
		}

		if (result == null) {
			result = getRegressionTimeWindowName(input);
		}

		return result;
	}

	private static String getRegressionTimeWindowName(RegressionInput input) {

		DateTime now = DateTime.now();

		DateTime activeWindow = now.minusMinutes(input.activeTimespan);
		DateTime baselineWindow = now.minusMinutes(input.baselineTimespan);

		PrettyTime prettyTime = new PrettyTime();

		String activeWindowDuration = prettyTime.formatDuration(new Date(activeWindow.getMillis()));
		String baselineDuration = prettyTime.formatDuration(new Date(baselineWindow.getMillis()));

		String result = String.format(TIME_WINDOW_FORMAT, activeWindowDuration, baselineDuration);

		return result;

	}

	private static String getRegressionDeploymentName(RegressionInput regressionInput, DateTime activeWindowStart) {

		PrettyTime prettyTime = new PrettyTime();

		String deployment;

		if (regressionInput.deployments.size() == 1) {
			deployment = regressionInput.deployments.iterator().next();
		} else {
			deployment = Arrays.toString(regressionInput.deployments.toArray());
		}

		DateTime baseline = DateTime.now().minusMinutes(regressionInput.baselineTimespan);
		String baselineDuration = prettyTime.formatDuration(new Date(baseline.getMillis()));
		String depFirstSeen = prettyTime.format(new Date(activeWindowStart.getMillis()));

		String result = String.format(DEPLOYMENT_FORMAT, deployment, depFirstSeen, baselineDuration);

		return result;
	}

	private static String getRegressionDeploymentName(ApiClient apiClient, RegressionInput regressionInput) {

		DateTime activeWindowStart = RegressionUtil.getDeploymentStartTime(apiClient, regressionInput, System.out);
		return getRegressionDeploymentName(regressionInput, activeWindowStart);

	}
}
