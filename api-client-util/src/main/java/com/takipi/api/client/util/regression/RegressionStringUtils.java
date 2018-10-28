package com.takipi.api.client.util.regression;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import com.takipi.api.client.result.event.EventResult;

public class RegressionStringUtils {
	
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

		;
		return event.type + " in " + simpleClassName + "." + event.error_location.method_name;
	}

	public static String getEventRate(EventResult event) {

		StringBuilder result = new StringBuilder();

		if ((event.stats.invocations == 0) || (event.stats.hits == 0)) {
			return "1";
		}

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
		
		return getRegressedEventRate(regressionResult.getEvent(), 
			regressionResult.getBaselineHits(), regressionResult.getBaselineInvocations());
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
}
