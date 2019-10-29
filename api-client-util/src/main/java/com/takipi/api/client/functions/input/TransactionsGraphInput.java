package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="transactionsGraph", type=FunctionType.Graph,
description = "A function returning a set of times series depicting the volume of calls into a target set \n" + 
		"of entry points (i.e. transactions)", 
	example="transactionsGraph({\"graphType\":\"view\",\"volumeType\":\"invocations\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
			"\"deployments\":\"$deployments\",\"servers\":\"$servers\",\"aggregate\":true,\n" + 
			"\"seriesName\":\"Throughput\",\"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\"})", 
	image="https://drive.google.com/file/d/1i_9DjK-mugjsagBKh-ZuJb3G07AtcyH6/view?usp=sharing", isInternal=false)
public class TransactionsGraphInput extends BaseGraphInput {
	
	public static final String AVG_TIME_GRAPH = "avg_time";
	public static final String INVOCATIONS_GRAPH = "invocations";
	public static final String ALL_GRAPH = "all";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={AVG_TIME_GRAPH, INVOCATIONS_GRAPH,ALL_GRAPH},
			defaultValue=AVG_TIME_GRAPH,
			description = "The volume type to be used for the Y value of each of the points in the time series: \n" +
				AVG_TIME_GRAPH + ": use the number of calls into the target transactions as the Y value\n" +
				INVOCATIONS_GRAPH +": return time series for the number of calls AND the avg response time to complete calls into the target transactions as the Y value\n" +
				ALL_GRAPH + ": the complete URL to the OverOps backend REST endpoint including port (i.e. api.overops.com:443)\n"
			)
	public String volumeType;
	
	public static final String AGGREGATE_YES = "Yes";
	public static final String AGGREGATE_NO = "No";
	public static final String AGGREGATE_AUTO = "Auto";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={AGGREGATE_YES, AGGREGATE_NO,AGGREGATE_AUTO},
			defaultValue=AGGREGATE_AUTO,
			description = "Controls whether the time series Y values for the matching transactions are merged into a single\n" + 
					" aggregate series: \n" +
					AGGREGATE_YES + ": aggregate the selected transactions into one series\n" +
					AGGREGATE_NO +": split the selected transactions into multiple series\n" +
					AGGREGATE_AUTO + ": aggregate the selected transactions into one series\n" + 
						"if the selection is a group or top transaction filter, otherwise split\n"
			)
	public String aggregateMode;
	
	public String getAggregateMode() {
		
		if ((aggregateMode == null)  || (aggregateMode.isEmpty())) {
			return AGGREGATE_AUTO;
		}
		
		return aggregateMode;
	}
		
	
	@Param(type=ParamType.Number, advanced=false, literals={},
			description = "Control the max number of separate time series returned by this function if aggregate is set to false",
			defaultValue = "0")
	public int limit;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited array of performance states, that a target transaction must meet in order to be returned\n" + 
					"by this function. Possible values are: 	"+  OK + ", " + SLOWING + ", " + CRITICAL,
			defaultValue = "")
	public String performanceStates;
	
	public static final String TIME_WINDOW_ACTIVE = "Active";
	public static final String TIME_WINDOW_BASELINE = "Baseline";
	public static final String TIME_WINDOW_ALL = "All";

	@Param(type=ParamType.Enum, advanced=false, 
			literals={TIME_WINDOW_ACTIVE, TIME_WINDOW_BASELINE,TIME_WINDOW_ALL},
			defaultValue=TIME_WINDOW_ACTIVE,
			description = "Control which time window points to use to calculate the weighted avg: \n" +
					TIME_WINDOW_ACTIVE + ": Use the avg response of the active time window as the Y value\n" +
					TIME_WINDOW_BASELINE +": Use the avg response of the baseline time window as the Y value\n" +
					TIME_WINDOW_ALL + ": Add the avg response of the active and baseline time windows as the Y value\n"
			)
	public String timeWindow;
	
	public String getTimeWindow() {
		
		String result = this.timeWindow;
		
		if (result == null) {
			result = TIME_WINDOW_ACTIVE;
		}
		
		return result;
	}
	
	public String timeFilterVar;
}
