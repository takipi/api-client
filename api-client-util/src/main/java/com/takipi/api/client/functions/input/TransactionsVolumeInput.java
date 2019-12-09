package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="transactionsVolume", type=FunctionType.Variable,
description = "  A function returning single stat volume of transactions (i.e. calls into event entry points)\n" + 
		"with a target set of envs, apps, deployments and servers. ", 
example="transactionsVolume({\"type\":\"sum\",\"volumeType\":\"invocations\",\"view\":\"$view\",\n" + 
		"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
		"\"servers\":\"$servers\", \"transactions\":\"$transactions\", \"searchText\":\"$searchText\",\n" + 
		"\"pointsWanted\":\"$transactionPointsWanted\"})", 
image="https://drive.google.com/file/d/11z_BD_B1Zno7uaWYGX4pTprJzqIwmtsJ/view?usp=sharing," +
		"https://drive.google.com/file/d/1i_9DjK-mugjsagBKh-ZuJb3G07AtcyH6/view?usp=sharing", 
		isInternal=false)

public class TransactionsVolumeInput extends BaseVolumeInput {
	
	public static final String INVOCATIONS_VOLUME = "invocations";
	public static final String AVG_VOLUME = "avg";
	public static final String COUNT_VOLUME = "count";

	@Param(type=ParamType.Enum, advanced=false, 
			literals={INVOCATIONS_VOLUME, AVG_VOLUME, COUNT_VOLUME},
			defaultValue=INVOCATIONS_VOLUME,
			description = "The volume type to return by querying this function \n" +
				INVOCATIONS_VOLUME + ": The number if calls into event entry points\n" +
				AVG_VOLUME +": The avg time of calls to entry points to complete\n" +
				COUNT_VOLUME + ": The unique number of transaction entry points\n" 
			)
	
	public String volumeType;		
}
