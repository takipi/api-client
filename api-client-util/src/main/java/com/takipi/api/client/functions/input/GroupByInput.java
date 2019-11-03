package com.takipi.api.client.functions.input;

import java.util.regex.Pattern;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="groupBy", type=FunctionType.Table,
description = " This function enables the user return event volume data that is \"grouped\" by a certain attribute\n" + 
		" of the events selected.", 
	example="groupBy({\"type\":\"sum\",\"field\":\"introduced_by\",\"limit\":5,\"volumeType\":\"all\",\n" + 
			" \"view\":\"All Events\",\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			" \"applications\":\"$applications\",\"servers\":\"$servers\",\n" + 
			" \"types\":\"Logged Error|Swallowed Exception|Logged Warning|Uncaught Exception|HTTP Error\",\n" + 
			" \"interval\":\"$__interval\"})", 
	image="https://drive.google.com/file/d/1iucN3zx2MSubKZx5I7UuJPs6dfB7P82F/view?usp=sharing", isInternal=false)

public class GroupByInput extends VolumeInput {
	
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String LOCATION = "location";
	public static final String LABEL = "labels";
	public static final String ENTRY_POINT = "entryPoint";
	public static final String INTRODUCED_BY = "introduced_by";
	public static final String APPLICATION = "application";
	public static final String SERVER = "server";
	public static final String DEPLOYMENT = "deployment";

	public static final String GROUP_BY_SERIES = "group_by";
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={NAME, LOCATION, ENTRY_POINT, INTRODUCED_BY, LABEL, APPLICATION, SERVER, DEPLOYMENT},
			defaultValue=APPLICATION,
			description = " The attribute by which to group volumes, can be any one of the values: \n" +
				TYPE + ": Group by event types (e.g. Logged Error,Swallowed Exception,..)\n" +
				NAME + ": Group by event names (e.g. Logged Error, NullPointerException,..)\n" +
				LOCATION +": Group by event locations (e.g classA.foo1, classB.foo2,..)\n" +
				ENTRY_POINT + ": Group by event entry points (e.g ServeletA.doGet, ServeletB.doGet,..)\n" +
				INTRODUCED_BY + ": Group event volume by application (e.g. Broker, Client, Bidder,..)\n" +
				LABEL + ": Group event volume by labels (e.g. Jira, Critical,..)\n" +
				APPLICATION + ": Group event volume by application (e.g. Broker, Client, Bidder,..)\n" +
				SERVER + ": Group events by the deployment in they occurred (e.g. v1.1, v1.2, v1.3,..). An event may be introduced\n" + 
						"	in a previous deployment (e.g. v1.3 which will match its introduced_by attribute) but surge due to\n" + 
						"	code or infrastructure changes in a subsequent deployment (e.g v1.5)" +
				DEPLOYMENT + "Group events by the deployment in they occurred (e.g. v1.1, v1.2, v1.3,..). An event may be introduced\n" + 
						"in a previous deployment (e.g. v1.3 which will match its introduced_by attribute) but surge due to\n" + 
						"code or infrastructure changes in a subsequent deployment (e.g v1.5).\n" + 
						"/")
	public String field;
		
	@Param(type=ParamType.Boolean, advanced=false, literals={}, defaultValue="false",
			description = "Control whether to create individual columns for each of the volume types returned by this query") 
	public boolean addTags;
	
	@Param(type=ParamType.Number, advanced=false, literals={}, defaultValue="0",
			description = "limit the number of applications / deployments / servers by which event volume is grouped to\n" + 
					"	 * the most recent <limit> deployments, or the the applications / servers which have the most event volume.\n") 
	public int limit;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="1d",
			description = "Set the time interval by which points in the returned time series are spaced. For example,\n" + 
				"an interval of 1d, means that a points containing the aggregated volume will be returned for each\n" + 
				"day within the time span (defined by the timeFilter parameter) executes.") 
	public String interval;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "A regex pattern used to filter in / out any value returned as the row name of each of the returned groups.\n" + 
				"For example, if the function is used to group event volume by application and the invoker \n" + 
				"would like to return rows for applications whose name starts with \"microservice\", the ^microservice regex\n" + 
				"can be provided. The same can be used for labels, deployments, servers and more.\n") 
	public String regexFilter;
	
	public Pattern getPatternFilter() {

		if (regexFilter == null) {
			return null;
		}

		if (patternFilter != null) {
			return patternFilter; 
		}
		
		patternFilter = Pattern.compile(regexFilter);
		
		return patternFilter;
	}
	
	private Pattern patternFilter;

}
