package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="views", type=FunctionType.Variable,
description = "This function populates a template variable containing a views within an optional category ", 
	example="views({\"environments\":\"$environments\", \"sorted\":true, \"category\":\"Tiers\"})	", 
	image=" https://drive.google.com/file/d/1FblzDqI5NeMA9Zt1rNdMDE36132kMCDR/view?usp=sharing", 
	isInternal=false)
public class ViewsInput extends BaseEnvironmentsInput {
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "An optional category name containing the views to return. If no value is provided all\n" + 
					"views within the selected environments are returned",
			defaultValue = "")
	public String category;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "Name of default view to use if none selected",
			defaultValue = "All Events")
	public String defaultView;
}
