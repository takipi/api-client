package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="labels", type=FunctionType.Variable,
description = "This function populates a variable containing all label names defined in the target environment(s)\n" + 
		" * matching an optional regex pattern", 
	example="labels({\"environments\":\"$environments\",\"sorted\":true, \"lablesRegex\":\"^infra\"})", 
	image="", 
	isInternal=false)
public class LabelsInput extends BaseEnvironmentsInput {
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "An optional regex pattern used to include / exclude label names from being returned by \n" + 
			"querying this function. For example, a value of \"^infra\" will only return labels whose \n" + 
			"name starts with \"infra\".") 
	public String lablesRegex;
}
