package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="applications", type=FunctionType.Variable,
description = "This function returns the list of active applications within the target environments. If\n" + 
		" application groups are defined in the Settings dashboard, they are added before the \n" + 
		" individual application names. ", 
	example="applications({\"environments\":\"$environments\",\"sorted\":\"true\"})", 
	image=" https://drive.google.com/file/d/1FblzDqI5NeMA9Zt1rNdMDE36132kMCDR/view?usp=sharing", 
	isInternal=false)
public class ApplicationsInput extends BaseEnvironmentsInput {
	
}
