package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.takipi.api.client.util.performance.calc.PerformanceState;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="transactionsList", type=FunctionType.Table,
description = "A function returning a list of rows depicting the volume and rates of calls into \n" + 
		" event entry points (i.e. transactions). This function can return a list of objects used to\n" + 
		" populate table, or a single stat value used to populate a single stat widget", 
	example="transactionsList({\"view\":\"$view\", \"timeFilter\":\"$timeFilter\",\n" + 
			"\"environments\":\"$environments\",\"applications\":\"$applications\",\"servers\":\"$servers\", \"deployments\":\"$deployments\", \n" + 
			"\"fields\":\"link,slow_delta,delta_description,from,to,timeRange,\n" + 
			"baseline_calls,active_calls,transaction,invocations,avg_response,baseline_avg,error_rate,errors\",\n" + 
			"\"renderMode\":\"Grid\",\"types\":\"$transactionFailureTypes\",\"transactions\":\"$transactions\", \n" + 
			"\"searchText\":\"$searchText\", \"pointsWanted\":\"$transactionPointsWanted\"})", 
	image="https://drive.google.com/file/d/1KFy__D3nhGi8HLvrKkiLfwT8YgIUmaN1/view?usp=sharing," +
			"https://drive.google.com/file/d/1A7eGLJIXvfUMHgvAcGEiY8uGTN1Xcn8X/view?usp=sharing", isInternal=false)

public class TransactionsListInput extends BaseGraphInput {
	
	public static final String RENDER_SINGLE_STAT = "SingleStat";
	public static final String RENDER_SINGLE_STAT_DESC = "SingleStatDesc";
	public static final String RENDER_SINGLE_STAT_VOLUME = "SingleStatVolume";
	public static final String RENDER_SINGLE_STAT_AVG = "SingleStatAvg";
	public static final String RENDER_SINGLE_STAT_BASELINE_AVG = "SingleStatBaselineAvg";
	public static final String RENDER_SINGLE_STAT_FAILURES = "SingleStatFailures";
	public static final String RENDER_SINGLE_STAT_FAIL_RATE = "SingleStatFailureRate";
	public static final String RENDER_GRID = "Grid";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={RENDER_SINGLE_STAT, RENDER_SINGLE_STAT_DESC, RENDER_SINGLE_STAT_VOLUME, 
					RENDER_SINGLE_STAT_AVG, RENDER_SINGLE_STAT_BASELINE_AVG, 
					RENDER_SINGLE_STAT_FAILURES, RENDER_SINGLE_STAT_FAIL_RATE, RENDER_GRID},
			defaultValue=RENDER_SINGLE_STAT,
			description = "Control whether to return a list of rows or a single aggregated stat by this function: \n" +
				RENDER_SINGLE_STAT + ": output the volume of transactions matching the target state (e.t. OK, Slowing, Critical)\n" +
				RENDER_SINGLE_STAT_DESC +": output a tooltip description of transactions matching the target state (e.t. OK, Slowing, Critical)\n" +
				RENDER_SINGLE_STAT_VOLUME + ": output the volume of transactions\n" +
				RENDER_SINGLE_STAT_AVG + ": output a weighted avg of transaction response time\n" +
				RENDER_SINGLE_STAT_BASELINE_AVG + ": output a number of failures in the list\n" +
				RENDER_SINGLE_STAT_FAILURES + ": output a number of failures divided by number of invocations in the list\n" +
				RENDER_SINGLE_STAT_FAIL_RATE + ": output a number of failures divided by number of invocations in the list\n" +
				RENDER_GRID  + ": output a row for each transaction")
	public String renderMode;
	
	public String getRenderMode() {
		
		if ((renderMode == null) || (renderMode.isEmpty())) {
			return RENDER_SINGLE_STAT;
		}
		
		return renderMode;	
	}
	
	public static final String LINK = "link";
	public static final String TRANSACTION = "transaction";	
	public static final String TOTAL = "invocations";
	public static final String AVG_RESPONSE = "avg_response";
	public static final String TIME_COMSUMED = "time_consumed";
	public static final String SLOW_STATE = "slow_state";	
	public static final String ERROR_RATE = "error_rate";
	public static final String ERRORS = "errors";
	public static final String ERROR_RATE_DELTA = "error_rate_state";
	public static final String ERROR_RATE_DELTA_STATE = "error_rate_delta_state";	
	public static final String ERROR_RATE_DELTA_DESC = "error_rate_delta_desc";
	public static final String ERRORS_DESC = "error_description";
	public static final String DELTA_DESC = "delta_description";
	public static final String BASELINE_AVG = "baseline_avg";
	public static final String BASELINE_CALLS = "baseline_calls";
	public static final String ACTIVE_CALLS = "active_calls";	
	
	public static final List<String> FIELDS = Arrays.asList(new String[] { 
			LINK, TRANSACTION, TOTAL, AVG_RESPONSE, BASELINE_AVG, BASELINE_CALLS, ACTIVE_CALLS, SLOW_STATE,
			DELTA_DESC, ERROR_RATE, ERRORS, ViewInput.TIME_RANGE });
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited array defining which fields to add for each returned row. If no value\n" + 
					"is specified, all fields are added. " + 
					LINK + ": a link pointing to the most recent Timer event set for the current entry point / trasnaction\n" + 
							" that has exceeded its threshold\n" +
					TRANSACTION + ": the name expressed as class.method of the current transaction\n" +
					TOTAL + " :the number of calls into the selected transaction\n" +
					AVG_RESPONSE + ": the avg response time in ms for the completion of the execution of the current transaction\n" + 
							"in in the selected time frame\n" +
					BASELINE_AVG + ": the avg response time during the baseline time range.\n" +
					BASELINE_CALLS + ": the number of calls for the current transaction during the baseline time range.\n" +
					ACTIVE_CALLS + ": a value showing the change between the event rate in the selected timeframe and the baseline\n" +
					TIME_COMSUMED + ": the avg response time * number of calls into the current transaction\n" + 
							" in in the selected time frame\n" +
					SLOW_STATE + ": the state of the transaction, either OK, SLOWING, or CRITICAL which describes based\n" + 
							"on the transaction slowdown algorithm the performance state of the current transaction in\n" + 
							"comparison to its respective baseline.\n" +
					ERRORS_DESC + ": a text description of the volume of errors of the type defined in the Settings dashboard a \"transaction failure types\".\n" +
					ERROR_RATE_DELTA + ": the diff in rate between the active and baseline window \n" + 
					ERROR_RATE_DELTA_STATE + ": an enum state comparing the rate between the active and baseline window \n" + 
							"with regression delta settings to output: OK, WARN, CRITICAL\n" +
					DELTA_DESC + ": a text description of the volume of errors of the type defined in the Settings dashboard a \"transaction failure types\".\n" +
					ERROR_RATE + ": the ratio between calls into the current transaction (invocations) and the volume of errors\n" + 
							"of the type defined in the Settings dashboard a \"transaction failure types\".\n" +
					ERRORS + ": the volume of errors of the type defined in the Settings dashboard a \"transaction failure types\".\n" +
					ViewInput.TIME_RANGE  + "the simple name of the event entry point class name\n",	
			defaultValue = "")
	public String fields;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A Java String format to be used when formatting the result of a single stat function call. ",
			defaultValue = "")
	public String singleStatFormat;
		
	@Param(type=ParamType.Enum, advanced=false, 
			literals={CRITICAL, SLOWING, OK},
			defaultValue=CRITICAL,
			description = "A | delimited array of performance states, that a target transaction must meet in order to be returned\n" + 
					"by this function. Possible values are:"  +
				CRITICAL + ": The transaction has slown down\n" +
				SLOWING +": The transaction is in the process of slowning down\n" +
				OK + ": The tranaction performance is OK\n")
	public String performanceStates;
	
	public static Collection<PerformanceState> getStates(String performanceStates) {
		
		List<PerformanceState> result = new ArrayList<PerformanceState>();
		
		if (performanceStates != null) {
			
			String[] parts = performanceStates.split(GRAFANA_SEPERATOR);
			
			for (String part : parts) {
				PerformanceState state = PerformanceState.valueOf(part);
				
				if (state == null) {
					throw new IllegalStateException("Unsupported state " + part + " in " + performanceStates);
				}
				
				result.add(state);
			}
		} else {
			for (PerformanceState state : PerformanceState.values()) {
				result.add(state);
			}
		}
		
		return result;
	}
	
}
