package com.takipi.api.client.functions.sample;

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
import com.takipi.api.client.functions.output.ReliabilityReportRow;
import com.takipi.api.client.functions.output.Series;
import com.takipi.api.client.functions.output.SeriesHeader;
import com.takipi.api.client.functions.output.SeriesRow;
import com.takipi.api.client.functions.output.TransactionRow;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.TimeUtil;

public class OverOpsFunctionsSample {
	
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
		
		for (Series series : response.data.getSeries()) {
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
		
		for (Series series : response.data.getSeries()) {
			
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
	 */
	public static void runReliabilityReport(StandaloneQueryApiClient apiClient, String serviceId) {
			
		ReliabilityReportInput input = new ReliabilityReportInput();
		
		input.environments = serviceId;
		input.timeFilter = TimeUtil.getLastWindowTimeFilter(TimeUnit.DAYS.toMillis(1)); //last day
		input.mode = ReliabilityReportInput.DEFAULT_REPORT; //each row to report an app
		input.limit = 5; //top 5 apps
		input.outputDrillDownSeries = true; //also return the drilldowns for new/inc/unique/vol/slow series for each app
		input.view = "sqs-general";
		
		Response<QueryResult> response = apiClient.get(input);
		
		if (response.isBadResponse()) {
			throw new IllegalStateException("Reliability report failed");
		}
			
		/*
		 * We can now iterate over the diff series returned by the reliability report:
		 * the main reliability_report series which is what you would see in the Home dashboard,
		 * and then for each of the 5 apps its drilldown quality gates as well:
		 * regressions (new/inc), event_volume, failure_volume (critical exceptions), slowdowns
		 * These are the exact data you would see in the drill down dashboards for each app:
		 * increasing errors, slowdowns, critical exceptions, etc..
		 */
		
		for (Series series : response.data.getSeries()) {
			
			System.out.println(series.name);
			
			printSeriesHeader(series);
			
			switch (series.type) {
				
				case ReliabilityReportInput.RELIABITY_REPORT_SERIES: {
					
					printReportScores(series);
					break;
				}
				
				case ReliabilityReportInput.REGRESSION_SERIES: {
					
					printRegressions(series);
					break;
				}
	
				case ReliabilityReportInput.FAILURES_SERIES: {
					printTopRankFailures(series, 5);
					break;
				}
				
				case ReliabilityReportInput.ERRORS_SERIES: {
					printTopEventsByVolume(series, 5);
					break;
				}
				
				case ReliabilityReportInput.SLOWDOWN_SERIES: {	
					printSlodowns(series);
					break;
				}
			}
		}
	}
	
	/*
	 * A series header describes which report key (e.g. app/dep/tier) this series
	 * describes. For example a "regressions" series for servideId: "S1234", key: "myApp"
	 * when reporting mode is set to "Applications" will describe any new / inc errors
	 * found in the "myApp" app in the "S1234" env.
	 */
	private static void printSeriesHeader(Series series) {
		
		SeriesHeader header = series.getHeader();
		
		if (header instanceof ReliabilityReportRow.Header) {
			ReliabilityReportRow.Header rrHeader = (ReliabilityReportRow.Header)header;
			System.out.println(rrHeader.serviceId + ", app = " + rrHeader.key);
		}
	}
	
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
	private static void printReportScores(Series series) {
		
		for (SeriesRow row : series) {
			ReliabilityReportRow rrRow = (ReliabilityReportRow)row;
			System.out.println("\t" +rrRow.key + "  score = " + rrRow.score);
		}
	}
	
	/*
	 * This series provides the information for the  the new / increasing quality gates
 	 * for a target row (e.g. app, dep) in the reliability report. Each row
 	 * describes either: new, sev new, inc, sev inc  issue
	 */
	private static void printRegressions(Series series) {
		
		for (SeriesRow row : series) {
			RegressionRow rgRow = (RegressionRow)row;
			System.out.println("\t" + rgRow.summary + "= " + rgRow.regression_type);
		}
	}
	
	/*
	 * This series is used to produce the information for "Critical Exceptions" quality gate, 
	 * based on which events pass the reliability report's "failureTypes" filter. 
	 * By default this would be "Critical Exceptions" which will read that list
	 * from the environment's settings. 
	 */
	private static void printTopRankFailures(Series series, int top) {
		
		int size = Math.min(series.size(), top);
		
		for (int i = 0; i < size; i++) {		
			EventRow evRow = (EventRow)series.readRow(i);
			System.out.println("\t" + evRow.summary + " rank  = " + evRow.rank);
		}
	}
	
	/*
	 * This series provides the information for the event volume and unique event count
	 * quality gates within a target report key (e.g. app, dep) 
	 */
	private static void printTopEventsByVolume(Series series, int top) {
		
		series.sort(EventsInput.HITS, false, true);
		
		int size = Math.min(series.size(), top);
		
		for (int i = 0; i < size; i++) {		
			EventRow evRow = (EventRow)series.readRow(i);
			System.out.println("\t" + evRow.summary + " volume  = " + evRow.hits);
		}
	}
	
	/*
	 * Each row in this series provides information for the slowdown quality gate
	 * for a target report key (e.g. app, dep) as well as all of its information: code location, slow state, avg response
	 */
	private static void printSlodowns(Series series) {
		
		for (SeriesRow row : series) {
			
			TransactionRow txRow = (TransactionRow)row;
			
			if ((txRow.slow_state == BaseEventVolumeInput.SLOWING_ORDINAL)
			|| (txRow.slow_state == BaseEventVolumeInput.CRITICAL_ORDINAL)) {
				String desc = BaseEventVolumeInput.TRANSACTION_STATES.get(txRow.slow_state);
				System.out.println("\t" +txRow.transaction + " slowdown state = " + desc);
			}
		}
	}
}
