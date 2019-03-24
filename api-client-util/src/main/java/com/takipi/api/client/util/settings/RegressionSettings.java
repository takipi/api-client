package com.takipi.api.client.util.settings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Strings;

public class RegressionSettings {

	/**
	 * The minimal baseline time period to use when comparing an event's volume in
	 * the current time frame and a baseline time frame
	 */
	public int min_baseline_timespan;

	/**
	 * The minimal ratio that must be maintained between the active window and
	 * baseline window. For example, if a active window of 2 weeks is selected and
	 * the baseline_timespan_factor is set to 4, the baseline window will be 8
	 * weeks.
	 */
	public int baseline_timespan_factor;

	/**
	 * The minimal volume an event must have within the active window for it to be
	 * considered regression analysis. Example value - 50.
	 */
	public int error_min_volume_threshold;

	/**
	 * The minimal rate between event volume and calls into the event location event
	 * must have within the active window for it to be considered regression
	 * analysis. For example for 10%, specify 0.1.
	 */
	public double error_min_rate_threshold;

	/**
	 * The minimal change in % between the relative volume of an event in the active
	 * time frame vs. that of the base line for it to be considered a regression.
	 * For example, a value of 0.2 specifies that is the relative rate (the ration
	 * between volume and calls) of an event has increased by more than 20% in the
	 * active and baseline period it will be considered to have regressed.
	 */
	public double error_regression_delta;

	/**
	 * The minimal change in % between the relative volume of an event in the active
	 * time frame vs. that of the base line for it to be considered a regression.
	 * For example, a value of 1 specifies that is the relative rate (the ration
	 * between volume and calls) of an event has increased by more than 100% in the
	 * active and baseline period it will be considered to have critically
	 * regressed.
	 */
	public double error_critical_regression_delta;

	/**
	 * Control whether to exclude an object that has had more than one slice /
	 * season within the baseline window exceed the volume of the active window, or
	 * 2 windows that have exceeded it by > 50% of the absolute volume of the active
	 * window. For example, if the active window is a day, and the baseline is set
	 * to two weeks, and the volume of events for a target event within the active
	 * window is 100, if any of the days in previous two weeks has seen more than
	 * 100 events, or two days have seen more than 50 events, that event will not be
	 * considered to have regressed
	 */
	public boolean apply_seasonality;

	/**
	 * A comma delimited list of event types that if a new event is introduced
	 * within the active time frame whose type is contained in the list, it will be
	 * considered a severe new issues, regardless of whether it exceeded the
	 * error_min_rate_threshold and error_min_volume_threshold settings. For
	 * example, a *new* NullPointerException may be considered severe, regardless of
	 * whether .or not it exceeded the minimal volume thresholds.
	 */
	public String critical_exception_types;

	public Collection<String> getCriticalExceptionTypes() {

		if (Strings.isNullOrEmpty(critical_exception_types)) {
			return Collections.emptyList();
		}

		Collection<String> result = Arrays.asList(critical_exception_types.split(ServiceSettingsData.ARRAY_SEPERATOR));

		return result;
	}
}
