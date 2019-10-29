package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="baselineAnnotation", type=FunctionType.Annotation,
description = "This query is used to populate a baseline graph annotation. This query will produce one point " + 
	" on all graphs within the dashboard that specifies where the baseline window ends and the active window " +
	" begin for regression and slowdown calculations",
 	example="baselineAnnotation({\"graphType\":\"view\",\"view\":\"$view\""  
 		+ "timeFilter\":\"time >= now() - $timeRange\",\"environments\":\"$environments\""  
	  	+ "\"applications\":\"$applications\", \"servers\":\"$servers\",\"deployments\":\"$deployments\"" 
	 	+ "\text\":\"<- baseline %s | active window %s  ->\"}",
	image="", isInternal=false)
public class BaselineAnnotationInput extends GraphInput {

	@Param(type=ParamType.String, advanced=false, literals={},
			description = "This value is used to String format the annotation text. It should be passed a String format\n" + 
				" which will be passed a string format %s value representing the baseline window value (e.g. 14d)\n" + 
				" and a second %s which will hold the value of the active window (e.g. 1d).",
			defaultValue = "")
	public String text;
}
