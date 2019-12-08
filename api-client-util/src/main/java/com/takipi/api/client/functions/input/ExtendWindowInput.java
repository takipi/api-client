package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="extendWindow", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class ExtendWindowInput extends ViewInput {
	public String range;
}
