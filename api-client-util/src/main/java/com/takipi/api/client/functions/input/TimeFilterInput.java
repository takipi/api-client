package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="timeFilter", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class TimeFilterInput extends VariableInput {
	public String timeFilter;
	public String limit;
	public boolean rangeOnly;
	public String rangePrefix;
}
