package com.takipi.api.client.functions.sample;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.takipi.api.client.functions.input.BaseEventVolumeInput;
import com.takipi.api.client.functions.input.BaseVolumeInput;
import com.takipi.api.client.functions.input.EventsInput;
import com.takipi.api.client.functions.input.GraphInput;
import com.takipi.api.client.functions.input.ReliabilityReportInput;
import com.takipi.api.client.functions.input.VolumeInput;
import com.takipi.api.client.functions.output.EventRow;
import com.takipi.api.client.functions.output.GraphRow;
import com.takipi.api.client.functions.output.QueryResult;
import com.takipi.api.client.functions.output.RegressionRow;
import com.takipi.api.client.functions.output.ReliabilityReport;
import com.takipi.api.client.functions.output.ReliabilityReport.ReliabilityReportItem;
import com.takipi.api.client.functions.output.ReliabilityReportRow;
import com.takipi.api.client.functions.output.Series;
import com.takipi.api.client.functions.output.SeriesRow;
import com.takipi.api.client.functions.output.TransactionRow;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.TimeUtil;

public class OverOpsFunctionsSample {
	
	private static final String TAB = "\t";
	private static final String TAB2 = "\t\t";

	public static void main(String[] args) {
		
		String apiServer = args[0]; // api REST endpoint (api.overops.cpm)
		String apiToken  = args[1]; // api token
		String appServer = args[2]; // function REST endpoint (ooai)
		String serviceId = args[3]; // S6295

		StandaloneQueryApiClient apiClient = StandaloneQueryApiClient.newBuilder()
				.setUsername(apiServer)
				.setHostname(appServer)
				.setPassword(apiToken)
				.build();
		
		runReliabilityReport(apiClient, serviceId);
		runGraph(apiClient, serviceId);
		runVolume(apiClient, serviceId);
	}
	
	/*
	 * This runs  the event volume function for the target service. By setting the "type"
	 * field to a value such as "-NullPointerException", the transaction field to "MyServlet.doGet,"
	 * applications to "myApp" or deployments to "v1.1" , the volume can be filtered.
	 * These filters and more are available for all event table, volume and graph functions 
	 */
	public static void runVolume(StandaloneQueryApiClient apiClient, String serviceId) {
		
		VolumeInput input = new VolumeInput();
		
		input.environments = serviceId;		
		input.timeFilter = TimeUtil.getLastWindowTimeFilter(TimeUnit.DAYS.toMillis(1));
		input.volumeType = VolumeType.all;
		input.type = BaseVolumeInput.SUM;
	
		Response<QueryResult> response = apiClient.get(input);
		
		if (response.isBadResponse()) {
			throw new IllegalStateException("volume failed");
		}
		
		for (Series<SeriesRow> series : response.data.getSeries()) {
			System.out.println("volume = " + series.getSingleStat());
		}
	}
	
	/*
	 * This runs charts the graph of event volume for the target env. Each point
	 * is returned as a timestamp value for the X axis and a double value for the Y axis 
	 */
	public static void runGraph(StandaloneQueryApiClient apiClient, String serviceId) {
		
		GraphInput input = new GraphInput();
		
		input.timeFilter = TimeUtil.getLastWindowTimeFilter(TimeUnit.DAYS.toMillis(1));
		input.volumeType = VolumeType.all;
		input.environments = serviceId;
		
		Response<QueryResult> response = apiClient.get(input);
		
		if (response.isBadResponse()) {
			throw new IllegalStateException("graph failed");
		}
		
		for (Series<SeriesRow> series : response.data.getSeries()) {
			
			System.out.println(series.name);
			
			for (int i = 0; i < series.values.size(); i++) {
				GraphRow row = (GraphRow)series.readRow(i);
				System.out.print(row.time + " " + row.value + ",");
			}
		}
	}
	
	/*
	 * This runs a full reliability report for the top 5 apps in the target env
	 * With the "outputDrillDownSeries" flag set, each of the 5 apps will also return
	 * all of its quality gate series: regressions (new/inc), error volume,
	 * critical error volume and slowdowns. 
	 * Each series' reader returns a matching row object which can be used to easily query 
	 * its values (e.g. score, slowdown state, ..) 
	 * This report feeds dashboards Home, cicd plugins (jenkins etc..), alerting UDFs
	 
	 /*
	 * Each row in this series provides the reliability output for a target
	 * report key (e.g. app, dep) based on the "reportMode" field of the function input.
	 * For each row, an associated set of 4 drill down series is also returned if
	 * the function input "outputDrilldownSeries" is set to true. The drilldown series
	 * will return 4 series for each of the rows (3 rows = 12 drill down series), providing:
	 * 	1: a list of errors for this report row for volume / unique error count
	 * 	2: a list of all new/inc errors for this report row
	 * 	3: a list of all errors matching the "failureTypes" filter (defaults to "Critical Exceptions") 
	 * 	4: a list of all slowndowns within this report row
	 * Each drilldown series has a header which describes which the serviceId and key (e.g. app/dep/tier)
	 * it relates to
	 */
	
	public static void runReliabilityReport(StandaloneQueryApiClient apiClient, String serviceId) {
			
		ReliabilityReportInput input = new ReliabilityReportInput();
		
		input.environments = serviceId;
		input.timeFilter = TimeUtil.getLastWindowTimeFilter(TimeUnit.DAYS.toMillis(4)); //last day
		input.mode = ReliabilityReportInput.APPS_EXTENDED_REPORT; //each row to report an app
		input.limit = 5; //top 5 apps
		input.outputDrillDownSeries = true; //also return the drilldowns for new/inc/unique/vol/slow series for each app
		
		ReliabilityReport reliabilityReport = ReliabilityReport.execute(apiClient, input);
		
		if (reliabilityReport == null) {
			throw new IllegalStateException("Reliability report failed");
		}
			
		for (Map.Entry<ReliabilityReportRow.Header, ReliabilityReportItem> entry : reliabilityReport.items.entrySet()) {
			
			ReliabilityReportRow.Header rrHeader = entry.getKey();
			ReliabilityReportItem rrItem = entry.getValue();
			
			printReportItemScore(rrHeader, rrItem);
			
			printReportNewErrors(rrItem);
			printReportIncErrors(rrItem);
			
			printReportItemFailures(rrItem, 5);
			printReportItemTopEventsByVolume(rrItem, 5);
			
			printReportItemSlowdowns(rrItem, true);
			printReportItemSlowdowns(rrItem, false);
		}
	}
	
	/*
	 * A series header describes which report key (e.g. app/dep/tier) this series
	 * describes. For example a "regressions" series for servideId: "S1234", key: "myApp"
	 * when reporting mode is set to "Applications" will describe any new / inc errors
	 * found in the "myApp" app in the "S1234" env.
	 */
	private static void printReportItemScore(ReliabilityReportRow.Header rrHeader, ReliabilityReportItem rrItem) {
		System.out.println(rrHeader.serviceId + ", app = " + rrHeader.key + " = " + rrItem.row.score);

	}
	
	/*
	 * This series provides the information for the  the "new errors" quality gates
 	 * for a target row (e.g. app, dep) in the reliability report. Each row
 	 * describes either: new, sev new issue
	 */
	private static void printReportNewErrors(ReliabilityReportItem rrItem) {
				
		System.out.println(TAB + "New Errors: ");
		
		for (RegressionRow row : rrItem.getNewErrors(false)) {
			System.out.println(TAB2 + row.summary + "= " + row.regression_type);
		}
	}
	
	/*
	 * This series provides the information for the  the "inc errors" quality gates
 	 * for a target row (e.g. app, dep) in the reliability report. Each row
 	 * describes either: inc, sev inc issue
	 */
	private static void printReportIncErrors(ReliabilityReportItem rrItem) {
		
		System.out.println(TAB + "Inc Errors: ");
		
		for (RegressionRow row : rrItem.geIncErrors(false)) {
			System.out.println(TAB2 + row.summary + "= " + row.regression_type);
		}
	}
	
	/*
	 * This series is used to produce the information for "Critical Exceptions" quality gate, 
	 * based on which events pass the reliability report's "failureTypes" filter. 
	 * By default this would be "Critical Exceptions" which will read that list
	 * from the environment's settings. 
	 */
	private static void printReportItemFailures(ReliabilityReportItem rrItem, int top) {
		
		System.out.println(TAB + "Top rank failures: ");
		
		int size = Math.min(rrItem.failures.size(), top);
		
		for (int i = 0; i < size; i++) {		
			EventRow evRow = rrItem.failures.readRow(i);
			System.out.println(TAB2 + evRow.summary);
		}
	}
	
	/*
	 * This series provides the information for the event volume and unique event count
	 * quality gates within a target report key (e.g. app, dep) 
	 */
	private static void printReportItemTopEventsByVolume(ReliabilityReportItem rrItem, int top) {
		
		System.out.println(TAB + "Top volume errors: ");

		rrItem.errors.sort(EventsInput.HITS, false, true);
		
		int size = Math.min(rrItem.errors.size(), top);
		
		for (int i = 0; i < size; i++) {		
			EventRow row = rrItem.errors.readRow(i);
			System.out.println(TAB2 + row.summary + " volume  = " + row.hits);
		}
	}
	
	/*
	 * Each row in this series provides information for the slowdown quality gate
	 * for a target report key (e.g. app, dep) as well as all of its information: code location, slow state, avg response
	 */
	private static void printReportItemSlowdowns(ReliabilityReportItem rrItem, boolean severe) {
		
		Collection<TransactionRow> rows;
		
		if (severe) {
			System.out.println(TAB + "Severe slowdowns: ");
			rows = rrItem.getSevereSlowdowns(); 
		} else {
			System.out.println(TAB + "Non severe slowdowns: ");
			rows = rrItem.getNonSevereSlowdowns();
		}
		
		for (TransactionRow row : rows) {
			String desc = BaseEventVolumeInput.TRANSACTION_STATES.get(row.slow_state);
			System.out.println(TAB2 +row.transaction + " slowdown state = " + desc);
		}
	}
}
