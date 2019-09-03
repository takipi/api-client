package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="variableRedirect", type=FunctionType.Variable,
description = "A function used to map the value of a template variable to provided constant. \n" + 
		"Used for internal purposes", example="", image="", isInternal=true)
public class VariableRedirectInput extends VariableInput {
	public String variable;
	public String dictionary;
}
