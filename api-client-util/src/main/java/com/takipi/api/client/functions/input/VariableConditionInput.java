package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="condition", type=FunctionType.Variable,
description = "A function used choose between two var values based on a condition value.\n" + 
		"Used for internal purposes", example="", image="", isInternal=true)
public class VariableConditionInput extends VariableInput {
	public String value;
	public String compareTo;
	public String trueValue;
	public String falseValue;
}
