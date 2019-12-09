package com.takipi.api.client.functions.input;

import java.util.Collection;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="limitVariable", type=FunctionType.Variable,
description = "Used for internal purposes to limit variable multiple selection into the\n" + 
		" first element when using within a URL of dashboard opened from a table link",
		example="", image="", isInternal=true)
public class LimitVariableInput extends VariableInput {
	
	public String name;
	
	public String values;
	
	public Collection<String> getValues() {
		return getServiceFilters(values, null, true);
	}
}
