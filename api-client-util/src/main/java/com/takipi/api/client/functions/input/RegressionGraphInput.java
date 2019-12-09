package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="regressionGraph", type=FunctionType.Graph,
description = "This function returns a list of time series data depicting volume increases within a target set of events.\n" + 
		" The algorithm used to decided which events to include is defined the Settings dashboard.\n", 
	example="regressionGraph({\"type\":\"sum\",\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \n" + 
			"\"applications\":\"$applications\", \"deployments\":\"$deployments\",\"servers\":\"$servers\",\n" + 
			"\"pointsWanted\":\"$pointsWanted\", \"types\":\"$type\",\"limit\":3,\n" + 
			"\"searchText\":\"$search\", \"graphType\":\"Absolute\"})", 
	image="", isInternal=false)

public class RegressionGraphInput extends GraphLimitInput {
	
	public static final String NEW_ISSUES = "NewIssues";
	public static final String REGRESSIONS = "Regressions";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={NEW_ISSUES, REGRESSIONS},
			defaultValue=REGRESSIONS,
			description = "Control the type of regressions returned by this graph. Default is Increasing / Regressed events.\n" + 
				NEW_ISSUES + ": return times series for new issues\n" +
				REGRESSIONS +": return times series for increasing / regressed issues")	
	public String regressionType;
	

	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A string postfix that would be added to the series names of all time series whose increase\n" + 
					"is deemed to be severe. For example, a P1 suffix can be added to each time series name whose volume increase\n" + 
					" is deemed severe.",
			defaultValue = "")
	public String sevSeriesPostfix;
	
	public static final String ABSOLUTE = "Absolute";
	public static final String PERCENTAGE = "Percentage";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={ABSOLUTE, PERCENTAGE},
			defaultValue=PERCENTAGE,
			description = "The value of each Y point returned by the time series results of this function\n" + 
				ABSOLUTE + ": The Y value of each point will denote the volume data of the event at that given point\n" + 
				PERCENTAGE +": The Y value of each point will denote the ratio between event volume and calls into the event location at that given point")
	public String graphType;
	
	public String getGraphType() {
		
		if ((graphType == null) || (graphType.isEmpty())) {
			return PERCENTAGE;
		}
		
		return graphType;
	}

	public String timeFilterVar;
}
