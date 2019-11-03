package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="fromTo", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class FromToInput extends VariableInput {
	
	public String timeFilter;
	
	public enum FromToType {
		From,
		To
	}
	
	public FromToType fromToType; 
	
	public FromToType getFromToType() {
		
		if (fromToType == null) {
			return FromToType.From;
		}
		
		return fromToType;
	}
}
