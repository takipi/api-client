package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="transactionsAvgGraph", type=FunctionType.Graph,
description = "A function returning a set of times series depicting the weighted avg response of calls into a target set \n" + 
		"of entry points (i.e. transactions)", 
	example="transactionsAvgGraph({\"graphType\":\"view\",\"volumeType\":\"invocations\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
			"\"deployments\":\"$deployments\",\"servers\":\"$servers\",\"aggregate\":true,\n" + 
			"\"seriesName\":\"Avg Response (ms)\",\"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\"})", 
	image="https://drive.google.com/file/d/1Hq-4iamqMxyDMRVb7nIQJVi1sW7R479Q/view?usp=sharing", isInternal=false)

public class TransactionAvgGraphInput extends TransactionsGraphInput {
	
}
