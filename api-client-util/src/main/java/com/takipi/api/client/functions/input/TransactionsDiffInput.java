package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="transactionsDiff", type=FunctionType.Graph,
description = "A function the returns a table containing the event diffs \n" + 
		"	between two groups of App/Dep/Server filters", 
	example="eventsDiff({\"fields\":\"link,type,entry_point,introduced_by,jira_issue_url,\n" + 
			"id,rate_desc,diff_desc,diff,message,error_location,stats.hits,rate,first_seen,\n" + 
			"jira_state\",\"view\":\"$view\",\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			"\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			"\"volumeType\":\"all\",\"maxColumnLength\":80, \"types\":\"$type\",\n" + 
			"\"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\", \n" + 
			"\"searchText\":\"$search\", \"compareToApplications\":\"$compareToApplications\", \n" + 
			"\"compareToDeployments\":\"$compareToDeployments\",\"compareToServers\":\"$compareToServers\", \n" + 
			"\"diffTypes\":\"Increasing\"})", 
	image="", isInternal=false)

public class TransactionsDiffInput extends TransactionsListInput {

	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = " A comma delimited array of application names to compare against")

	public String baselineApplications;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "A comma delimited array of server names to compare against")

	public String baselineServers;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "A comma delimited array of deployment names  to compare against")

	public String baselineDeployments;
}
