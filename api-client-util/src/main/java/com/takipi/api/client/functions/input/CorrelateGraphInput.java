package com.takipi.api.client.functions.input;

import java.util.Collection;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="correlateGraph", type=FunctionType.Graph,
description = "This function charts the throughput of the selected trasnactions or a selected system metric", 
	example="correlateGraph({\"graphType\":\"view\",\"volumeType\":\"invocations\",\"view\":\"$view\"" + 
		"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\"" + 
		"deployments\":\"$deployments\",\"servers\":\"$servers\",\"aggregateMode\":Yes" +
		"seriesName\":\"Throughput\",\"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\", \"metricNames\":\"$metricNames\"})",
	image="", 
	isInternal=false)

public class CorrelateGraphInput extends TransactionsGraphInput {
	
	public static final String THROUGHPUT_METRIC = "Throughput";
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "The selected system metric or transaction throughput",
			defaultValue = "CPU")
	public String metricNames;	
	
	public Collection<String> getMetricNames() {
		return SystemMetricsGraphInput.getMetricNames(metricNames);
	}
}
