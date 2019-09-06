package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.takipi.api.client.util.performance.calc.PerformanceState;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="reliabilityReport,regressionReport", type=FunctionType.Table,
description = "A function returning a report for a a target set of applications, deployments or tiers (i.e. categories)\n" + 
		"listing an possible new errors, increases (i.e. regressions) and slowdowns. The function can return\n" + 
		"either a tabular report or a chart, where a key selected value from each row is used as the Y value\n" + 
		"and the name of the target app, deployment, tier is used as the X value. The decisions regarding\n" + 
		"which events are new, regressing and which transactions have slowdowns are governed by the \n" + 
		"regression and transaction setting available in the Settings dashboard.", 
	example="Example query for table:\n" + 
			"regressionReport({\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			"\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			"\"view\":\"$view\",\"pointsWanted\":\"$pointsWanted\", \"transactionPointsWanted\":\"$transactionPointsWanted\",\n" + 
			"\"types\":\"$type\", \"render\":\"Grid\", \"mode\":\"Tiers\", \"limit\":\"$limit\", \n" + 
			"\"sevAndNonSevFormat\":\"%d  (%d p1)\", \"sevOnlyFormat\":\"%d p1\"})\n" + 
			"Example query for graph:\n" + 
			"regressionReport({\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			"\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			"\"view\":\"$view\",\"pointsWanted\":\"$pointsWanted\", \"transactionPointsWanted\":\"$transactionPointsWanted\",\n" + 
			"\"types\":\"$type\", \"render\":\"Graph\", \"mode\":\"Deployments\", \"limit\":\"$limit\",\n" + 
			"\"graphType\":\"$graphType\"})", 
	image="Screenshot: https://drive.google.com/file/d/1aEXcfTGC9OfNaJvsEeRptqp2o1czd0SW/view?usp=sharing\n" + 
			"", isInternal=false)

public class ReliabilityReportInput extends RegressionsInput {
	
	/**
	 * Available reporting modes
	 */
	public static final String APPLICATIONS_REPORT = "Applications";
	public static final String APPS_EXTENDED_REPORT = "Apps_Extended";
	public static final String DEPLOYMENTS_REPORT = "Deployments";
	public static final String TIERS_REPORT = "Tiers";
	public static final String TIERS_EXTENDED_REPORT = "Tiers_Extended";
	public static final String TIMELINE_REPORT = "Timeline";
	public static final String TIMELINE_EXTENDED_REPORT = "Timeline_Extended";
	public static final String DEFAULT_REPORT = "Default";

	/**
	 * Available reliability KPIs
	 */
	public static final String NEW_ERRORS_KPI = "NewErrors";
	public static final String SEVERE_NEW_ERRORS_KPI = "SevereNewErrors";
	public static final String ERROR_INCREASES_KPI = "ErrorIncreases";
	public static final String SEVERE_ERROR_INCREASES_KPI = "SevereErrorIncreases";
	public static final String SLOWDOWNS_KPI = "Slowdowns";
	public static final String SEVERE_SLOWDOWNS_KPI = "SevereSlowdowns";
	public static final String ERROR_VOLUME_KPI = "ErrorVolume";
	public static final String ERROR_COUNT_KPI = "ErrorCount";
	public static final String ERROR_RATE_KPI = "ErrorRate";
	public static final String FAIL_RATE_DELTA_KPI = "FailRateDelta";
	public static final String FAIL_RATE_DESC_KPI = "FailRateDesc";
	public static final String SCORE_KPI = "Score";
	public static final String SCORE_DESC_KPI = "ScoreDesc";

	public static final List<String> KPIS = Arrays.asList(new String[] {NEW_ERRORS_KPI, SEVERE_NEW_ERRORS_KPI,
		ERROR_INCREASES_KPI, SEVERE_ERROR_INCREASES_KPI, SLOWDOWNS_KPI, SEVERE_SLOWDOWNS_KPI,
		ERROR_RATE_KPI, FAIL_RATE_DELTA_KPI, FAIL_RATE_DESC_KPI, SCORE_KPI, SCORE_DESC_KPI});
	
	public static String getKpi(String kpi) {
		
		if ((kpi ==  null) || (kpi.length() == 0)) {
			return SCORE_KPI;
		}
		
		String result;
		String value = kpi.replace(" ", "");
		
		if (!KPIS.contains(value)) {
			result = SCORE_KPI;	
		} else {
			result = value;
		}
		
		return result;
	}
	
	public static final String RELIABITY_REPORT_SERIES = "reliability_report_series";

	public static final String REGRESSION_SERIES = "regressions";
	public static final String ERRORS_SERIES = "errors";
	public static final String FAILURES_SERIES = "failures";
	public static final String SLOWDOWN_SERIES = "slowdown";

	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Control whether to report only on live deployment in Deployment report mode",
			defaultValue = "false")
	public boolean liveDeploymentsOnly;
		
	@Param(type=ParamType.Enum, advanced=false, literals={Day, Week, Hour},
			description = "Control the report interval in Timeline mode",
			defaultValue = Day)
	public String reportInterval;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = " Control whether to add am \"Application\" tier  in Tiers report mode",
			defaultValue = "false")
	public boolean addAppTier;
	
	public static final String KPIS_DESC = 
			NEW_ERRORS_KPI + ": chart the number of new issues\n" +
			SEVERE_NEW_ERRORS_KPI +": chart the number of severe increasing errors \n" +
			ERROR_INCREASES_KPI + ": chart the number of increasing errors \n" +
			SEVERE_ERROR_INCREASES_KPI + ": chart the number of severe increasing errors \n" +
			ERROR_RATE_KPI + ": the URL without a port value  (i.e 443)\n" +
			SLOWDOWNS_KPI + ": chart the number of transaction slowdowns\n" +
			SEVERE_SLOWDOWNS_KPI + ": chart the number of severe transaction slowdowns\n" + 
			FAIL_RATE_DELTA_KPI + ": chart the transaction failure rate delta\n" +
			FAIL_RATE_DESC_KPI + ": chart the transaction failure rate delta description\n" +
			SCORE_KPI + ": chart the reliability score of the target app, deployment, tier\n" +
			SCORE_DESC_KPI  + ": chart the score desc of the target app, deployment, tier";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={NEW_ERRORS_KPI, SEVERE_NEW_ERRORS_KPI, 
					ERROR_INCREASES_KPI, SEVERE_ERROR_INCREASES_KPI, 
					SLOWDOWNS_KPI, SEVERE_SLOWDOWNS_KPI, 
					ERROR_RATE_KPI, FAIL_RATE_DELTA_KPI, FAIL_RATE_DESC_KPI, 
					SCORE_KPI, SCORE_DESC_KPI},
			defaultValue=SCORE_KPI,
			description = "The specific kpi to chart \n" + KPIS_DESC
				)
	public String graphType;
		
	@Param(type=ParamType.Enum, advanced=false, 
			literals={APPLICATIONS_REPORT, APPS_EXTENDED_REPORT, 
					DEPLOYMENTS_REPORT, TIERS_REPORT, 
					TIERS_EXTENDED_REPORT, TIMELINE_REPORT, 
					TIMELINE_EXTENDED_REPORT,DEFAULT_REPORT},
			defaultValue=DEFAULT_REPORT,
			description = "The target filters to report each row by: application, deployment or tier\n" +
					APPLICATIONS_REPORT + ": The report will return a row for each application in the environment, the report\n" + 
							" will list up to <limit> apps, sorted by the apps who have the highest volume of events\n" + 
					APPS_EXTENDED_REPORT +":The report will return a row for each application in the environment with additional\n" + 
							"information about throughput and failure rates. The report\n" + 
							"will list up to <limit> apps, sorted by the apps who have the highest volume of events\n" +
					DEPLOYMENTS_REPORT + ": The report will return a row for the last <limit> deployments in the target envs.\n" +
					TIERS_REPORT + ", " + TIERS_EXTENDED_REPORT + "The report will return a row for the last <limit> tiers in the target envs which \n" + 
							"have the highest volume of events. If any key tiers are defiend in the Settings dashboard\n" + 
							"they are used first." +
					TIERS_EXTENDED_REPORT + ": the URL without a port value  (i.e 443)\n" +
					TIMELINE_REPORT + "," + TIMELINE_EXTENDED_REPORT + ":  The report will return a a single row each day within the timeframe with extended information\n" +
					DEFAULT_REPORT + ": The report will return a a single row for the target event set\n\n" +
					" In grid mode the follwing fields are available to choose from: \n" + FIELDS_DESC
				)
	public String mode;
	
	public String getReportMode() {
		
		if ((mode == null) || (mode.isEmpty())) {
			return DEFAULT_REPORT;
		}
		
		return mode;	
	}
	
	public boolean isTiersReportMode() {
		return TIERS_REPORT.equals(mode) || TIERS_EXTENDED_REPORT.equals(mode);
	}
	
	/**
	 * Control the type of events used to compute the report score
	 *
	 */
	public enum ScoreType {
		
		/**
		 * Include regression analysis for new events only
		 */
		NewOnly(true, false),
		
		/**
		 * Include regression analysis for new and increasing events
		 */
		Regressions(true, false),
		
		/**
		 * Include slowdown analysis 
		 */
		Slowdowns(false, true),
	
		/**
		 * Combine regression and slowdown analysis in the output (default)
		 */
		Combined(true, true);
		
		private final boolean includeRegressions;
		private final boolean includeTransactions;
		
		ScoreType(boolean includeRegressions, boolean includeTransactions) {
			this.includeRegressions = includeRegressions;
			this.includeTransactions = includeTransactions;
		}
		
		public boolean includeSlowdowns() {
			return includeTransactions;
		}
		
		public boolean includeRegressions() {
			return includeRegressions;
		}
	}
	
	public ScoreType scoreType;
	
	public ScoreType getScoreType() {
		
		if (scoreType == null) {
			return ScoreType.Combined;
		}
		
		return scoreType;
	}
		
	@Param(type=ParamType.Number, advanced=false, literals={},
			description = "The max number of rows / time series points to return",
			defaultValue = "0")
	public int limit;
	
	@Param(type=ParamType.Boolean, advanced=true, literals={},
			description = "Control whether non-key apps added to report are sorted by volume",
			defaultValue = "false")
	public boolean queryAppVolumes;
	
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited pair of numeric values used to define thresholds by which\n" + 
			"to choose a postfix for a score series based on the values set in postfixes",
			defaultValue = "")
	public String thresholds;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited arrays of 3 postfix to be added to the series name. The post fix\n" + 
				"is selected based on if the reliability score is smaller than, in between or greater than the upper\n" + 
				"threshold. For example if this is set to \"BAD,OK,GOOD\" and thresholds is \"70,85\"\n" + 
				"a score below 70 will have the postfix \"BAD\" added to the series name, 80 will be\n" + 
				"\"OK\" and 90 will be \"GOOD\" ",
			defaultValue = "")
	public String postfixes;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The string format used to present a row value containing both severe and non severe items\n" + 
					"will receive the number of issues and the number of severe issues as string format %d parameters,\n" + 
					"for example: \"%d  (%d p1)\"",
			defaultValue = "%d  (%d p1)")
	public String sevAndNonSevFormat;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The string format used to present a row value containing non severe items\n" + 
				"will receive the number of issues as string format %d parameter,\n" + 
				"for example: \"%d issues",
			defaultValue = "%d p1")
	public String sevOnlyFormat;
	
	
	public static final String SORT_ASCENDING = "Ascending";
	public static final String SORT_DESCENDING = "Descending";
	public static final String SORT_DEFAULT = "Default";

	@Param(type=ParamType.Enum, advanced=true, 
			literals={SORT_ASCENDING, SORT_DESCENDING, 
					SORT_DEFAULT},
			defaultValue=SORT_DEFAULT,
			description = "Control how rows are sorted")
	public String sortType;	
	
	public String getSortType() {
		
		if (sortType == null) {
			return SORT_DEFAULT;
		}
		
		return sortType;
	}
		
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "An optional comma delimited list of types to be used to calculate\n" + 
					"failure types in extended app reporting mode. If no value if provided the types value\n" + 
					"is used.",
			defaultValue = "")
	public String failureTypes;
	
	public String getFailureTypes() {
		
		if ((failureTypes == null) || (failureTypes.isEmpty())) {	
			if ((types != null) && (!types.isEmpty())) {
				return types;
			} else {
				return BaseEventVolumeInput.CRITICAL_EXCEPTIONS_FILTER;
			}
		}
		
		return failureTypes;
	}
	
	/**
	 * The diff reliability state calculated for an app in extended reporting mode
	 * this applies to slowdown, new, increasing and transaction fail rate states 
	 *
	 */
	public enum ReliabilityState {
		@SuppressWarnings("hiding")
		OK,
		Warning,
		Severe
	}
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited array used to visually annotate the reliability status of the key\n" + 
					"based on the score ranges",
			defaultValue = "\\xE2\\x9C\\x85,\\xE2\\x9A\\xA0\\xEF\\xB8\\x8F,\\xE2\\x9D\\x8C")
	public String statusPrefixes;
		
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited array used to visually annotate the fail rate status of the key\n" + 
					" based on the score ranges",
			defaultValue = "\\xE2\\x9C\\x85,\\xE2\\x9A\\xA0\\xEF\\xB8\\x8F,\\xE2\\x9D\\x8C")
	public String failRatePrefixes;
		
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited array used to visually annotate the alert status of the key\n" + 
					"with values for: no alerts (add), new error, anomaly",
			defaultValue = "\\xE2\\x9E\\x95,\\xF0\\x9F\\x86\\x95\\xEF\\xB8\\x8F,\\xF0\\x9F\\x93\\x88")
	public String alertStatusPrefixes;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A string postfix to be added to an app / tier name to denote it having alerts set",
			defaultValue = "")
	public String alertNamePostfix;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "A comma delimited array in the form of X,Y, where X defines the threshold for a failed score\n" + 
					"and Y defines the threshold a successful score",
			defaultValue = "70,85")
	public String scoreRanges;
	
	public static final String NEW_ERROR_FEED = "NewError";
	public static final String SEVERE_NEW_ERROR_FEED = "SevereNewError";
	public static final String INCREASING_ERROR_FEED = "IncreasingError";
	public static final String SEVERE_INCREASING_ERROR_FEED = "SevereIncreasingError";
	public static final String SLOWDOWN_FEED = "Slowdown";
	public static final String SEVERE_SLOWDOWN_FEED = "SevereSlowdown";
	
	public static List<String> FEED_TYPES = Arrays.asList(new String[] {NEW_ERROR_FEED,
		SEVERE_NEW_ERROR_FEED, INCREASING_ERROR_FEED, SEVERE_INCREASING_ERROR_FEED,
		SLOWDOWN_FEED, SEVERE_SLOWDOWN_FEED});
	
	@Param(type=ParamType.Enum, advanced=true, 
			literals={NEW_ERROR_FEED, SEVERE_NEW_ERROR_FEED, 
					INCREASING_ERROR_FEED, SEVERE_INCREASING_ERROR_FEED, 
					SLOWDOWN_FEED, SEVERE_SLOWDOWN_FEED},
			defaultValue="",
			description = "Control the type of anomlies presented within the output feed\n" +
					NEW_ERROR_FEED + ": predent new non severe errors\n" + 
					SEVERE_NEW_ERROR_FEED +": present severe errors\n" + 
					INCREASING_ERROR_FEED + ": present errors whose volume has increased when compared to their baseline\n" +
					SEVERE_INCREASING_ERROR_FEED + ": present severe errors whose volume has increased when compared to their baseline\n" +
					SLOWDOWN_FEED + ": present Transactions whose response time has slown down comapred to their baseline \n" +
					SEVERE_SLOWDOWN_FEED + ": present Transactions whose response time has severly slown down comapred to their baseline\n")
	public String eventFeedTypes;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The id of the dashboard to which new errors drill into",
			defaultValue = "MKvwYc7Wk")
	public String newErrorDashboardId;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The id of the dashboard to which inc errors drill into",
			defaultValue = "WZ739NYmk")
	public String incErrorDashboardId;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The id of the dashboard to which slowdown events drill into",
			defaultValue = "iHSORcymk")
	public String slowdownDashboardId;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The var name within the new errors dashboard to which an ARC link for this error will be set ",
			defaultValue = "link")
	public String newErrorDashboardField;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The var name within the inc errors dashboard to which the code location this error will be set ",
			defaultValue = "increasingErrors")
	public String incErrorDashboardField;
	
	@Param(type=ParamType.String, advanced=true, literals={},
			description = "The var name within the slowdowns dashboard to which the transaction name for this slowdown will be set ",
			defaultValue = "transactions")
	public String slowdownDashboardField;
	
	@Param(type=ParamType.Boolean, advanced=true, literals={},
			description = "Control whether to output additional series " +
					ERRORS_SERIES + "," + FAILURES_SERIES + "," + FAILURES_SERIES + "," + REGRESSION_SERIES
					+ " detailing the contents of errors and slowdowns",
			defaultValue = "false")
	public boolean outputDrillDownSeries;
	
	public static String getFeedEventType(String type) {
		
		switch (type) {
			case NEW_ISSUE_REGRESSIONS: 
				return NEW_ERROR_FEED;
			case SEVERE_NEW_ISSUE_REGRESSIONS: 
				return NEW_ERROR_FEED;
			case SEVERE_INC_ERROR_REGRESSIONS: 
				return SEVERE_INCREASING_ERROR_FEED;
			case INC_ERROR_REGRESSIONS: 
				return INCREASING_ERROR_FEED;
			default: 
				throw new IllegalStateException(String.valueOf(type));
		}
	}
		
	public static String getFeedEventType(PerformanceState state) {
			
		switch (state) {
			case CRITICAL:
				return SEVERE_SLOWDOWN_FEED;
			case SLOWING:
				return SLOWDOWN_FEED;
			case OK:
			case NO_DATA:
				return null;
			default:
				throw new IllegalStateException(String.valueOf(state));
		}
	}		
	
	public Collection<String> getEventFeedTypes() {
		
		if ((eventFeedTypes ==  null) || (eventFeedTypes.length() == 0)) {
			return null;
		}
		
		Collection<String> values = getServiceFilters(eventFeedTypes, null, true);
		Set<String> result = new HashSet<String>(values.size());

		for (String value : values) {
			
			String feedType = value.replace(" ", "");
			
			if (FEED_TYPES.contains(feedType)) {
				result.add(feedType);
			}
		}
			
		return result;
	}
	
	/**
	 * Below are the constants describing the field supported by this function for each of the available 
	 * report modes
	 */
	
	public static final String SERVICE = "Service";
	public static final String KEY = "Key";
	public static final String KEY_NANE= "Name";
	public static final String PREV_DEP_NAME = "previousDepName";
	public static final String PREV_DEP_FROM = "previousDepFrom";			
	public static final String PREV_DEP_STATE =  "previousDepState";
	public static final String TIMELINE_DIFF_STATE =  "TimelineDiffState";
	public static final String NEW_ISSUES = "NewIssues";
	public static final String REGRESSIONS = "Regressions";
	public static final String SLOWDOWNS = "Slowdowns"; 
	public static final String NEW_ISSUES_DESC = "NewIssuesDesc";
	public static final String REGRESSIONS_DESC = "RegressionsDesc";
	public static final String SLOWDOWNS_DESC = "SlowdownsDesc";
	public static final String SCORE = "Score"; 
	public static final String SCORE_DESC = "ScoreDesc";
	public static final String TRANSACTION_VOLUME = "TransactionVolume";
	public static final String TRANSACTION_COUNT = "TransactionCount";
	public static final String TRANSACTION_AVG_RESPONSE = "TransactionAvgResponse";
	public static final String TRANSACTION_RESPONSE_DELTA = "TransactionResponseDelta";
	public static final String TRANSACTION_FAILURES = "TransactionFailures";
	public static final String TRANSACTION_VOLUME_DESC = "TransactionVolumeDesc";
	public static final String TRANSACTION_FAIL_COUNT_DESC = "TransactionFailureCountDesc";
	public static final String TRANSACTION_FAILURES_COUNT = "TransactionFailureCount";
	public static final String TRANSACTION_FAIL_RATE = "TransactionFailRate";
	public static final String TRANSACTION_FAIL_RATE_DELTA = "TransactionFailRateDelta";
	public static final String ERROR_VOLUME = "ErrorVolume";
	public static final String ERROR_COUNT_DESC = "ErrorCountDesc";
	public static final String ERROR_COUNT = "ErrorCount";
	public static final String RELIABILITY_STATE = "ReliabilityState";
	public static final String TRANSACTION_FAIL_DESC = "FailureDesc";
	public static final String RELIABILITY_DESC = "RelabilityDesc";
	public static final String STATUS_NAME = "StatusName";
	public static final String ALERT_STATUS = "AlertStatus";
	public static final String ALERT_DESC = "AlertDesc";
	public static final String CONNECTED_CLIENTS = "ConnectedClients";
	public static final String CONNECTED_CLIENTS_DESC = "ConnectedClientsDesc";
	public static final String EVENT_NAME = "EventName";
	public static final String EVENT_DESC = "EventDesc";
	public static final String EVENT_TYPE_DESC = "EventTypeDesc";
	public static final String EVENT_TYPE = "EventType";
	public static final String EVENT_APP = "EventApp";
	public static final String DASHBOARD_ID = "DashboardId";
	public static final String DASHBOARD_FIELD = "DashboardField";
	public static final String DASHBOARD_VALUE = "DashboardValue";
	
	public static final String FIELDS_DESC = 
		SERVICE + ": the env ID for this row\n" +
		KEY + ": the target app/dep/tier\n" +
		KEY_NANE + ": a human-readable name  of the target app/dep/tier\n" +
		PREV_DEP_NAME + ": name of the deployment, the name of the deployment to be diff against\n" +
		PREV_DEP_FROM + ": name of the deployment, the start of the deployment to be diff against\n" +
		PREV_DEP_STATE + ": name of the deployment and a diff deployment exists = 1, otherwise 0\n" +
		TIMELINE_DIFF_STATE + ": The field name of the row used to link the user to the diff of the current timeline interval and its prev one\n" +
		NEW_ISSUES + ": name of the row is a Value of any new issues in this row\n" +
		REGRESSIONS + ": name of the value of any regressions in this row\n" +
		SLOWDOWNS + ": the value of any slowdowns in this row\n" +
		NEW_ISSUES_DESC + ": the Text description of new issues in this row\n" +
		REGRESSIONS_DESC + ": the Text description of regressions in this row\n" +
		SLOWDOWNS_DESC + ": the Text description of slowdowns in this row\n" +
		SCORE + ": the Reliability score for this row\n" +
		SCORE_DESC + ": text description of reliability score for this row\n" +
		TRANSACTION_VOLUME + ": transaction volume for this row\n" +
		TRANSACTION_COUNT + ":  transaction count for this row\n" +
		TRANSACTION_AVG_RESPONSE + ": transaction avg response for this row\n" +
		TRANSACTION_FAILURES + ": transaction failure volume\n" +
		TRANSACTION_VOLUME_DESC + ": transaction failure count description\n" +
		TRANSACTION_FAIL_COUNT_DESC + ": transaction failure count description\n" +
		TRANSACTION_FAILURES_COUNT + ": transaction failure unique count\n" +
		TRANSACTION_FAIL_RATE + ": transaction failure rate for this row\n" +
		TRANSACTION_FAIL_RATE_DELTA + ": transaction failure delta rate for this row\n" +
		ERROR_VOLUME + ": error count for this row\n" +
		ERROR_COUNT_DESC + ": error count desc for this row\n" +
		ERROR_COUNT + ": error count for this row\n" +
		RELIABILITY_STATE + ": overall reliability state\n" +
		TRANSACTION_FAIL_DESC + ": transaction failure rate description\n" +
		RELIABILITY_DESC + ": app reliability state description\n" +
		STATUS_NAME + ": an app name + reliability state\n" +
		ALERT_STATUS + ": an app / tier alert status\n" +
		ALERT_DESC + ": app / tier alert description\n" +
		CONNECTED_CLIENTS + ": number of connected clients of app\n" +
		CONNECTED_CLIENTS_DESC + ": number of description of connected clients of app\n" +
		EVENT_NAME + ": name of the message of a feed event\n" +
		EVENT_DESC + ":  description of a feed event\n" +
		EVENT_TYPE_DESC + ": description of a feed event type\n" +
		EVENT_TYPE + ": type for the current event feed item\n" +
		EVENT_APP + ": app for the current event feed item\n" +
		DASHBOARD_ID + ": name of the drill down dashboard for the current event feed item\n" +
		DASHBOARD_FIELD + ": name of the drill down field within the drill down dashboard for the current event feed item\n" +
		DASHBOARD_VALUE + ": name of the drill down field value within the drill down dashboard for the current event feed item\n";

	/**
	 * The list of default fields returned for dep reporting
	 */
	public static final List<String> DEFAULT_DEP_FIELDS = Arrays.asList(
		new String[] { 	
			ViewInput.FROM,
			ViewInput.TO, 
			ViewInput.TIME_RANGE, 
			SERVICE, 
			KEY, 
			NEW_ISSUES_DESC, 
			REGRESSIONS_DESC, 
			SLOWDOWNS_DESC,
			SCORE_DESC,
			PREV_DEP_NAME, 
			PREV_DEP_FROM,
			KEY_NANE,
			NEW_ISSUES, 
			REGRESSIONS, 
			SLOWDOWNS,
			PREV_DEP_STATE,
			SCORE, 		
		});
	
	/**
	 * The list of default fields returned for app / tier reporting
	 */
	public static final List<String> DEFAULT_APP_FIELDS = Arrays.asList(
		new String[] { 	
			ViewInput.FROM, 
			ViewInput.TO, 
		 	ViewInput.TIME_RANGE, 
		 	SERVICE, 
		 	KEY, 
		 	NEW_ISSUES_DESC, 
		 	REGRESSIONS_DESC, 
			SLOWDOWNS_DESC,
		 	SCORE_DESC,
		 	KEY_NANE, 
			NEW_ISSUES, 
		  	REGRESSIONS, 
		 	SLOWDOWNS, 		 	
		 	SCORE
		});
	
	/**
	 * The list of default fields returned for timeline reporting
	 */
	public static final List<String> DEFAULT_TIMELINE_FIELDS = Arrays.asList(
		new String[] { 	
			ViewInput.FROM, 
			ViewInput.TO, 
		 	ViewInput.TIME_RANGE, 
		 	SERVICE, 
		 	KEY, 
		 	NEW_ISSUES_DESC, 
		 	REGRESSIONS_DESC, 
			SLOWDOWNS_DESC,
		 	SCORE_DESC,
		 	KEY_NANE, 
			NEW_ISSUES, 
		  	REGRESSIONS, 
		 	SLOWDOWNS, 
		 	TIMELINE_DIFF_STATE,
		 	SCORE
		});
	
	/**
	 * The list of default fields returned for extended app reporting
	 */
	public static final List<String> DEFAULT_EXTENDED_FIELDS = Arrays.asList(
		 new String[] { 	
			ViewInput.FROM, 
			ViewInput.TO, 
			ViewInput.TIME_RANGE, 
			SERVICE, 
			KEY, 
		 	RELIABILITY_STATE,
			NEW_ISSUES_DESC, 
			REGRESSIONS_DESC, 
			SLOWDOWNS_DESC,
			SCORE_DESC,	
			TRANSACTION_FAIL_DESC,
			RELIABILITY_DESC,
			ALERT_DESC,
			TRANSACTION_FAIL_COUNT_DESC,
			ERROR_COUNT_DESC,
			TRANSACTION_VOLUME_DESC,
			CONNECTED_CLIENTS_DESC,
			KEY_NANE, 
			STATUS_NAME,
		 	ALERT_STATUS,
			SCORE,
		 	NEW_ISSUES, 
		  	REGRESSIONS, 
		  	TRANSACTION_FAILURES,
		 	TRANSACTION_FAILURES_COUNT,
		 	SLOWDOWNS, 
			TRANSACTION_FAIL_RATE, 
		 	TRANSACTION_FAIL_RATE_DELTA,
		 	ERROR_VOLUME, 
		 	ERROR_COUNT,
		 	TRANSACTION_COUNT,
		 	TRANSACTION_VOLUME,
			TRANSACTION_AVG_RESPONSE,
			CONNECTED_CLIENTS
			
		});

	
	/**
	 * The list of default fields returned for an event feed report
	 */
	public static final List<String> FEED_FIELDS = Arrays.asList(
			 new String[] { 	
				ViewInput.FROM, 
				ViewInput.TO, 
				ViewInput.TIME_RANGE, 
				SERVICE,
				KEY,
				DASHBOARD_ID,
				DASHBOARD_FIELD,
				DASHBOARD_VALUE,
				EVENT_DESC,
				EVENT_TYPE_DESC,
				EVENT_TYPE,
				EVENT_NAME,
				EVENT_APP
		});	
}
