package com.takipi.api.client.util.settings;

import java.util.List;
import java.util.regex.Pattern;

import com.takipi.api.client.util.infra.Categories.Category;

public class ServiceSettingsData {
	public static final String ARRAY_SEPERATOR_RAW = ",";
	public static final String ARRAY_SEPERATOR = Pattern.quote(ARRAY_SEPERATOR_RAW);

	/**
	 * General settings related to how data is presented and ordered in table and
	 * graph widgets
	 */
	public GeneralSettings general;

	/**
	 * A list of pattern groups that defined key business transaction that can be
	 * specifically reported on and visualized within Grafana dashboards. For
	 * example, a user may defined all servlets within a target set of packages as
	 * having key importance to the reliability of the applications and as such
	 * would like to be able to report specifically on any new errors, regressions
	 * and slowdowns within them.
	 */
	public GroupSettings transactions;

	/**
	 * A list of pattern groups that defined key application groups that can be
	 * specifically reported on and visualized within Grafana dashboards. For
	 * example, a user may defined a group microservices related to payment, as the
	 * "Payment" app group and have the ability to report on any new errors,
	 * regressions and slowdowns within them as a single logical unit.
	 */
	public GroupSettings applications;

	/**
	 * A list of pattern groups that defined key high level packages groups that can
	 * be specifically reported on and visualized within Grafana dashboards. For
	 * example, a user may define all code nested within the com.acme.payment top
	 * package as the "Payment" to and have the ability to report on any new errors,
	 * regressions and slowdowns within them as a single logical unit. OverOps used
	 * a set of out of the box categorizations defined in: https://git.io/fpPT0 to
	 * provide default tiers that can be reported on.
	 */
	public List<Category> tiers;

	/**
	 * The settings used to determine for a target entry point called into by the
	 * app whether its avg response time is slowing down in comparison to a baseline
	 * period.
	 */
	public SlowdownSettings slowdown;

	/**
	 * The settings used to whether an event active within the current time frame is
	 * considered new or regressed (increasing), and if so, at what severity level.
	 */
	public RegressionSettings regression;

	/**
	 * The settings used to provide a high-level reliability score for target app,
	 * deployment, tier based on the number of severe and non severe new events,
	 * regressions and slowdowns occuring within it within the current time frame.
	 */
	public RegressionReportSettings regression_report;
}
