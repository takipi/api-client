package com.takipi.api.client.functions.input;
import com.takipi.api.client.functions.input.GraphLimitInput;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="appsGraph", type=FunctionType.Graph,
description = "This function returns the volume of events within a target set of events split by application\n" + 
		"which can rendered in a widget.", 
	example="appsGraph({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\n" + 
			"\"environments\":\"$environments\", \"applications\":\"$applications\", \"servers\":\"$servers\", \n" + 
			"\"deployments\":\"$deployments\",\"pointsWanted\":\"$pointsWanted\",\"types\":\"$type\",\n" + 
			"seriesName\":\"Times\", \"transactions\":\"$transactions\", \"searchText\":\"$search\"})\n", 
	image="", isInternal=false)

public class AppsGraphInput extends GraphLimitInput {
	
}
