package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="keyTransactionsGraph", type=FunctionType.Graph,
description = "A function returning a set of times series depicting the volume of calls into a all transactions marked as key", 
	example="transactionsGraph({\"graphType\":\"view\",\"volumeType\":\"invocations\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
			"\"deployments\":\"$deployments\",\"servers\":\"$servers\",\"aggregate\":true,\n" + 
			"\"seriesName\":\"Throughput\",\"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\"})", 
	image="https://drive.google.com/file/d/1i_9DjK-mugjsagBKh-ZuJb3G07AtcyH6/view?usp=sharing", isInternal=false)
public class KeyTransactionsGraphInput extends TransactionsGraphInput {
	
}
