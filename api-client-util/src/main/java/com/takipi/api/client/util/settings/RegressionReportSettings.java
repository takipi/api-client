package com.takipi.api.client.util.settings;

/**
 * Scoring the reliability of a target application, deployment of tier is done
 * as following: score = 100 - score_weight * ( new_event_score * newEventCount
 * + severe_new_event_score * severeNewEventCount + regression_score *
 * (regressionCount + slowdownCount) + severe_regression_score *
 * (severeRegressionCount + severeSlowdownCount)) / days_in_time_range)
 * 
 * The score is averaged over the number of days within the selected time frame
 * so that if a deployment that was introduced yesterday and has already
 * introduced 5 new errors will not receive the same score as that of one which
 * was introduced a month ago and during that time has only introduced 5 new
 * errors, and as such will be considered more stable.
 *
 */
public class RegressionReportSettings {

	/**
	 * The number of points deducted for a new event.
	 */
	public double new_event_score;

	/**
	 * The number of points deducted for a regressed event.
	 */
	public double regression_score;

	/**
	 * The number of points deducted for a severe new event.
	 */
	public double severe_new_event_score;

	/**
	 * The number of points deducted for a severe regression or slowdown.
	 */
	public double critical_regression_score;

	/**
	 * A factor applied to the score deduction of a new error, regression or
	 * slowdown.
	 */
	public double score_weight;

	/**
	 * A factor applied to the score deduction of a new error, regression or
	 * slowdown in a key app or tier.
	 */
	public double key_score_weight;
}
