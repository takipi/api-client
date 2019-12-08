package com.takipi.api.client.functions.input;

import java.util.Collection;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="systemMetricsGraph", type=FunctionType.Graph,
description = " This function charts a selected set of system metrics such as CPU, GC, Memory,..", 
	example="systemMetricsGraph({\"graphType\":\"view\",\"volumeType\":\"invocations\",\"view\":\"$view\"," +
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\", " +
			"\"deployments\":\"$deployments\",\"servers\":\"$servers\",\"aggregateMode\":Yes,\"seriesName\":\"CPU\"," +
			"\"pointsWanted\":\"$transactionPointsWanted\",\"transactions\":\"$transactions\"," +
			" \"metricNames\":\"CPU Usage\"})", 
	image="", isInternal=false)

public class SystemMetricsGraphInput extends BaseGraphInput {
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="CPU Usage",
			description = "A | seperated list of system metrics to chart. " +
					"This can be any of the values returned by the systemMetricsMetadata function")
	public String metricNames;
	
	public static final String DEFAULT_POSTFIX = "*";
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue=DEFAULT_POSTFIX,
			description = "An optional postfix to be appended to the name of result series")
	public String metricPostfix;
	
	public String getMetricPostfix() {
		
		if (metricPostfix == null) {
			return DEFAULT_POSTFIX;
		}
		
		return metricPostfix;
	}
	
	public Collection<String> getMetricNames() {
		return getMetricNames(metricNames);
	}

	public static Collection<String> getMetricNames(String metricNames) {
		return getServiceFilters(metricNames, null, false);
	}
}
