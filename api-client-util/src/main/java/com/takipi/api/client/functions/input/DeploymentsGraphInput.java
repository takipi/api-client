package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

	@Function(name="deploymentsGraph", type=FunctionType.Graph,
	description = "This function will return a set of graphs representing the volume of events" +
		" within active deployments",
	example="\"deploymentsGraph({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"All\"+ Events\",\"+\n" + 
		"\"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\"+ \n" + 
		"\"\"applications\":\"$applications\",\"+ \"\"servers\":\"$servers\",\"deployments\":\"$deployments\",\"+\n" + 
		"\"\"graphCount\":3})\"", 
	image="https://drive.google.com/file/d/1LJ_kMUkykxSWVVK5ixlhRsjTiJPjhYHS/view?usp=sharing", isInternal=false)
	public class DeploymentsGraphInput extends GraphInput{
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A limit on the number of graphs or deployment annotations that will be returned by this function", 
			defaultValue = "0")
	public int limit;
}
