package com.takipi.api.client.functions.input;

import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="graph", type=FunctionType.Graph,
description = " This function returns the volume of events within a target set of events whose attributes match\n" + 
		"those defined in the EventFilter section of this funtion's parameters list in time series format\n" + 
		"which can rendered in a widget.", 
	example="graph({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\n" + 
			"\"environments\":\"$environments\", \"applications\":\"$applications\", \"servers\":\"$servers\", \n" + 
			"\"deployments\":\"$deployments\",\"pointsWanted\":\"$pointsWanted\",\"types\":\"$type\",\n" + 
			"seriesName\":\"Times\", \"transactions\":\"$transactions\", \"searchText\":\"$search\"})\n", 
	image="https://drive.google.com/file/d/10yJ3zHbHZiWIfbsd9cePv8oG_EcmGvCk/view?usp=sharing", isInternal=false)

public class GraphInput extends BaseGraphInput {
	
	@Param(type=ParamType.Enum, advanced=false, literals={VOLUME_TYPE_HITS, VOLUME_TYPE_INVOCATIONS, VOLUME_TYPE_ALL}, defaultValue=VOLUME_TYPE_ALL,
			description = "The type of event stats used to populate the Y values of points returned by this function. Values:\n" + 
			"hits, all: the Y values will represent the volume of matching events.\n" + 
			"invocations: the Y values will represent the number of calls into matching events. \n")
	public VolumeType volumeType;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={}, defaultValue="false",
			description = "Control whether this graph is shown in a grafana single stat widget sparrkline,\n" + 
			" in which case multiple env selection will return a single aggregate volume point") 
	public boolean sparkline;
}

