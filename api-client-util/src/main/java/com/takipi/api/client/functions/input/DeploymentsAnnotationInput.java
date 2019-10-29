package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="deploymentsAnnotation", type=FunctionType.Annotation,
description = "This function will produce a list of points each holding the epoch time\n" + 
	" representing when a deployment was introduced into this environment as well as its name",
	example="  	\"deploymentsAnnotation({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"All Events\",\" +\n" + 
	"\"\"timeFilter\":\"time >= now() - 14d\",\"environments\":\"$environments\",\" + \n" + 
	"\"\"applications\":\"$applications\",\"+ \"\"servers\":\"$servers\",\"deployments\":\"$deployments\",\"+\n" + 
	"\"\"seriesName\":\"Times\",\"graphCount\":3})\"", 
	image="", isInternal=false)
public class DeploymentsAnnotationInput extends DeploymentsGraphInput {
	
}
