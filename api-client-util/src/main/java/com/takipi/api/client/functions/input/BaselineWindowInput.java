package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="baselineWindow", type=FunctionType.Variable,
description = "This query is used to populate a variable holding the value of the current baseline window." + 
	"This query will produce one value that can be used for both presentation purposes inside" + 
	"widget titles as well as to set and Time window overrides for widgets which are used "+
 	"display graphs that show any regression and slowdown calculations.",
example="baselineWindow({\"graphType\":\"view\",\"view\":\"$view\",\n" + 
		" \"timeFilter\":\"timeFilter\",\"environments\":\"$environments\",\n" + 
		" \"applications\":\"$applications\", \"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
		" \"windowType\":\"Active\"})", image="", isInternal=false)
public class BaselineWindowInput extends BaseEventVolumeInput {
	
	public static final String Active = "Active";
	public static final String Baseline = "Baseline";
	public static final String Combined = "Combined";
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "Control whether output is returned in minute format (e.g. \"90m\"), \n" + 
					" or if true in human readable format (e.g. \"An hour an half ago\").",
			defaultValue = "")
	public boolean prettyFormat;
	
	@Param(type=ParamType.Enum, advanced=false, literals={Active,Baseline,Combined},
			description = "Set whether to return the combined value of the active, baseline window,\n" + 
			" or their combined time span. Default is Active." +
			Active + ": Return the active time window\n" +
			Baseline +": Return the baseline time window\n" +
			Combined + ":  Return the active and baseline window combined",
			defaultValue = Active)
	public String windowType;
	
	public String getWindowType() {
		
		if (windowType == null) {
			return Active;
		}
		
		return windowType;
	}
}
