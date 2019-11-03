package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="routingGraph,tiersGraph", type=FunctionType.Graph,
description = " A function which returns time series for event volume grouped by tier. Key tiers\n" + 
		" * are returned first followed by tiers whose event volume is the highest.", 
	example="routingGraph({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \n" + 
			"\"applications\":\"$applications\", \"servers\":\"$servers\", \"deployments\":\"$deployments\",\n" + 
			"\"pointsWanted\":\"$transactionPointsWanted\",\"types\":\"$type\",\n" + 
			"\"transactions\":\"$transactions\", \"limit\":\"$splitLimit\"})", 
	image="https://drive.google.com/file/d/1qBk94hF_3hPaAH-52rmX5FdbGrM-hVJD/view?usp=sharing", isInternal=false)

public class TiersGraphInput extends GraphLimitInput {
	
}
