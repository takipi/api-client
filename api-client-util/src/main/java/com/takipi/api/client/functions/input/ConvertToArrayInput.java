package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="convertToArray", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class ConvertToArrayInput extends VariableInput {
	public String array;
	public String prefix;
	public String postfix;
}
