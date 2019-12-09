package com.takipi.api.client.functions.input;

import java.util.Collection;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="typesGraph", type=FunctionType.Graph,
description = "A function returning time series data of events matching a target filter, broken down by event type.", 
	example="typesGraph({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\n" + 
			"\"environments\":\"$environments\", \"applications\":\"$applications\", \"servers\":\"$servers\",\n" + 
			"\"deployments\":\"$deployments\",\"pointsWanted\":\"$pointsWanted\",\"types\":\"$type\",\n" + 
			"\"seriesName\":\"Times\", \"transactions\":\"$transactions\", \"searchText\":\"$search\",\n" + 
			"\"defaultTypes\":\"Logged Error|Uncaught Exception|HTTP Error\"})", 
	image="", isInternal=false)

public class TypesGraphInput extends GraphInput {
	
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A | delimited array of event types for which time series volume data would be split.",
			defaultValue = "")
	public String defaultTypes;
	
	public Collection<String> getDefaultTypes() {

		if (!hasFilter(defaultTypes)) {
			return null;
		}

		return getServiceFilters(defaultTypes, null, true);
	}
}
