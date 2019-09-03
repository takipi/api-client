package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="eventsDiff", type=FunctionType.Table,
description = "A function that compares two different combinations of a application/deployment/server\n" + 
		"filter set, returning events that are either new or have regressed between the different\n" + 
		" filters. This is especially useful for comparing errors between to different versions.\n", 
	example="eventsDiff({\"fields\":\"link,type,entry_point,introduced_by,jira_issue_url,\n" + 
			" id,rate_desc,diff_desc,diff,message,error_location,stats.hits,rate,\n" + 
			" first_seen,jira_state\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\n" + 
			" \"environments\":\"$environments\",\"applications\":\"$applications\",\n" + 
			" \"servers\":\"$servers\",\"deployments\":\"$deployments\",\"volumeType\":\"all\",\n" + 
			" \"maxColumnLength\":80, \"types\":\"$type\",\"pointsWanted\":\"$pointsWanted\",\n" + 
			" \"transactions\":\"$transactions\", \"searchText\":\"$search\",\n" + 
			" \"compareToApplications\":\"$compareToApplications\", \n" + 
			" \"compareToDeployments\":\"$compareToDeployments\",\n" + 
			" \"compareToServers\":\"$compareToServers\", \"diffTypes\":\"Increasing\"})", 
	image="https://drive.google.com/file/d/1l6ARZfTCR3UfBh649uO_VhAipeX8njZ4/view?usp=sharing", 
	isInternal=false)
public class EventsDiffInput extends EventsInput {
		
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited array of application names to compare against", 
			defaultValue = "")
	public String compareToApplications;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited array of server names to compare against", 
			defaultValue = "")
	public String compareToServers;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited array of deployment names  to compare against", 
			defaultValue = "")
	public String compareToDeployments;
	
	public static final String New = "New";
	public static final String Increasing = "Increasing";

	@Param(type=ParamType.Enum, advanced=false, literals={},
			description = "Comma delimited list of diff types to display" + 
				New + ": Event is new in target release.\n" +
				Increasing +": Event is increased in target release.",
			defaultValue = "")
	public String diffTypes; 
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A string describing a time unit to be used as an offset when comparing the filters." + 
					"For example setting 24h, will compare the current time window with a time window " + 
					"stating and ending 24h before",
			defaultValue = "")
	public String timeDiff;
		
	@Param(type=ParamType.Number, advanced=false, literals={},
			description = "Define the top number of results",
			defaultValue = "")
	public String limit;
	
	/**
	 * Additional available fields
	 */
	public static final String DIFF = "diff";
	public static final String DIFF_DESC = "diff_desc";
	
	public Collection<String> getDiffTypes() {
		
		if (diffTypes == null) {
			return Collections.emptyList();
		}
		
		String[] parts = diffTypes.split(ARRAY_SEPERATOR);
		Collection<String> result = new ArrayList<String>(parts.length);
		
		for (String part : parts) {
			
			if ((New.equals(part))|| (Increasing.equals(part))) {
				result.add(part);
			} else {
				throw new IllegalStateException("Unknown diff type " + part);
			}
		}
		
		return result;
		
	}
}
