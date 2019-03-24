package com.takipi.api.client.util.settings;

public class SlowdownSettings {
	/**
	 * The minimal number of calls into a target entry point within the selected
	 * time frame for it to be considered for slowdown analysis,
	 */
	public int active_invocations_threshold;

	/**
	 * The minimal number of calls into a target entry point within the baseline
	 * time frame for it to be considered for slowdown analysis.
	 */
	public int baseline_invocations_threshold;

	/**
	 * The minimal change (in ms) between the avg response time within the selected
	 * time frame and baseline for a transaction's state to be marked as Slowing or
	 * Critical.
	 */
	public int min_delta_threshold;

	/**
	 * The percentage change (between 0 and 1) that the number of calls within the
	 * active timeframe whose response time exceeds the avg of the baseline + the
	 * std dev of the baseline * std_dev_factor, for a call to be considered
	 * slowing. For example, if the number of calls in the last week whose avg
	 * response time is 90ms, where the baseline avg was 60ms and the std dev was
	 * 5s, is more than 0.3 (30%) of the calls, the transaction will be marked as
	 * slowing.
	 */
	public double over_avg_slowing_percentage;

	/**
	 * The percentage change (between 0 and 1) that the number of calls within the
	 * active timeframe whose response time exceeds the avg of the baseline + the
	 * std dev of the baseline * std_dev_factor, for a call to be considered
	 * critical. For example, if the number of calls in the last week whose avg
	 * response time is 90ms, where the baseline avg was 60ms and the std dev was
	 * 5s, is more than 0.6 (60%) of the calls, the transaction will be marked as
	 * critical.
	 */
	public double over_avg_critical_percentage;

	/**
	 * The number of additional std devs (as calculated from the baseline time
	 * frame) added to the current baseline avg when calculating the percentage of
	 * calls exceeding that combined value for a transaction to be considered slow
	 * or slowing.
	 */
	public double std_dev_factor;
}
