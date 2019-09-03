package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="concat", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class ConcatInput extends VariableInput {
	public String a;
	public String b;
	public String separator;
}
