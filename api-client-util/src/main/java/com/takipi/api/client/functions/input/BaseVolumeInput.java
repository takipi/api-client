package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * Basic input for all functions which returns a single stat volume value
 */
public abstract class BaseVolumeInput extends BaseEventVolumeInput  {
	
	public static final String SUM = "sum";
	public static final String AVG = "avg";
	public static final String COUNT = "count";

	public static final List<String> AGGREGATAION_TYPES = Arrays.asList(new String[] {SUM, AVG, COUNT});
	
	@Param(type=ParamType.Enum, advanced=false, literals={},
			description = " A comma delimited array of the types of volume returned by this query. Values: \n" 
				 + SUM + " :return a sum of all values returned by this function\n"
				 + AVG + ": return an avg of all values returned by this function\n"
				 + COUNT +  ": return a unique count of all values returned by this function",
			defaultValue = SUM)
	public String type;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Control whether values are returned in numeric format or string format (e.g. 1000 vs \"1K\")", 
			defaultValue = "")
	public boolean stringValue;
}
