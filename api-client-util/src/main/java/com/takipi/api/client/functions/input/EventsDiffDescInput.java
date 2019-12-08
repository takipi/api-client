package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="eventsDiffDesc", type=FunctionType.Variable,
description = "A function used to describe the components of an event diff ", example="", image="", isInternal=true)
public class EventsDiffDescInput extends EventsDiffInput {
	
	public static final String FilterA = "FilterA";
	public static final String FilterB = "FilterB";
	public static final String Combined = "Combined";

	@Param(type=ParamType.Enum, advanced=false, 
			literals={FilterA, FilterB, Combined},
			defaultValue=Combined,
			description = "The URL type to return as one of the HostType options \n" +
			FilterA + " describe group A filter" +
			FilterB +"describe group B filter" +
			Combined + "describe both groups")
	
	public String descType;
	
	public String getDescType() {
		
		if (descType == null) {
			return Combined;			
		}
		
		return descType;
	}
		
	@Param(type=ParamType.Enum, advanced=false, 
			literals={FilterA, FilterB, Combined},
			defaultValue=Combined,
			description = "The URL type to return as one of the HostType options \n" +
			FilterA + " describe group A filter" +
			FilterB +"describe group B filter" +
			Combined + "describe both groups")
	public String diffType;
	
	public String getDiffType() {
		
		if (diffType == null) {
			return New;			
		}
		
		return diffType;
	}
}
