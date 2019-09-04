package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * The base input for all functions that are used to provide data for graph widget.
 *
 */
public abstract class BaseGraphInput extends BaseEventVolumeInput {
	
	public static final String GRAPH_SERIES = "graph";
	
	@Param(type=ParamType.Number, advanced=false, literals={},
		description = "If no pointsWanted resolution is specified for the query this value can be used to determine " + 
		"the number of graph points that would passed to any graph function invoke by this query. " +
		"The number of points will be calculated as the the query's time span (t2-t1) /  interval",
		defaultValue = "0")
	public long interval;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = " The name that would be given to a graph series returned by this query. This may be overriden\n" + 
			" by derivative graphs.",
			defaultValue = "")
	public String seriesName;
}
