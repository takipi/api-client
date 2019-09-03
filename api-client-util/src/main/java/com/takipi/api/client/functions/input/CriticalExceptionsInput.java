package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="criticalExceptionsGraph", type=FunctionType.Graph,
description = " This function charts the volume of exceptions whose type is defined as critical within the Settings console" +
		" within a target set of events whose attributes match\n" + 
		"those defined in the EventFilter section of this funtion's parameters list in time series format\n" + 
		"which can rendered in a widget.", 
	example="criticalExceptions({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\n" + 
			"\"environments\":\"$environments\", \"applications\":\"$applications\", \"servers\":\"$servers\", \n" + 
			"\"deployments\":\"$deployments\",\"pointsWanted\":\"$pointsWanted\",\"types\":\"$type\",\n" + 
			"seriesName\":\"Times\", \"transactions\":\"$transactions\", \"searchText\":\"$search\"})\n", 
	image="", isInternal=false)

public class CriticalExceptionsInput extends GraphLimitInput {
	
}
