package com.takipi.api.client.functions.input;

import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="transactionsRate", type=FunctionType.Variable,
description = "A function used to return a single stat depicting the rate between calls into a target set of events entry points\n" + 
		"and the event volume. This could be used to show the ratio between calls into events entry points (i.e. transaction throughput)\n" + 
		"and a specific type of errors taking place within them (e.g transaction failures).", 
example="transactionsRate({\"type\":\"sum\",\"volumeType\":\"invocations\",\"view\":\"$view\",\n" + 
		"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
		"\"deployments\":\"$deployments\",\"servers\":\"$servers\",\"types\":\"$transactionFailureTypes\",\n" + 
		"\"transactions\":\"$transactions\", \"filter\":\"events\",\"searchText\":\"$searchText\", \n" + 
		"\"transactionSearchText\":\"$searchText\",\"pointsWanted\":\"$transactionPointsWanted\"})", 
image="https://drive.google.com/file/d/1__-49ejQq0TAiRZ2l7sJC16nZRzA7kQZ/view?usp=sharing", 
		isInternal=false)
public class TransactionsRateInput extends TransactionsVolumeInput {
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={VOLUME_TYPE_HITS, VOLUME_TYPE_INVOCATIONS, 
					VOLUME_TYPE_ALL},
			defaultValue=VOLUME_TYPE_ALL,
			description = "the event volume type which serves as the denominator between throughput and volume returned \n" + 
					" by this function:\n" +
					VOLUME_TYPE_HITS + ": aggregate volume for all event that had hit volume\n" + 
					VOLUME_TYPE_INVOCATIONS +": aggregate the ratio between even volume and the calls into events locations\n" + 
					VOLUME_TYPE_ALL +":  aggregate volume for all events with either hits or invocations available\n")
	public VolumeType eventVolumeType;
	
	public static final String TRANSACTION_FILTER_EVENTS = "events";
	public static final String TRANSACTION_FILTER_TIMERS = "timers";

	
	/**
	 * The type of events whose volume is use to include in the denominator
	 */
	@Param(type=ParamType.Enum, advanced=false, 
			literals={TRANSACTION_FILTER_EVENTS, TRANSACTION_FILTER_TIMERS},
			defaultValue=TRANSACTION_FILTER_EVENTS,
			description = "The type of events whose volume is use to include in the denominator:\n" +
					TRANSACTION_FILTER_EVENTS + ": include only events of type Timer. This can be used to produce the ration between\n" + 
						"throughput (i.e. calls into event entry points) and the number s of times timers\n" + 
						"set within them have exceeded their target thresholds\n" + 
					TRANSACTION_FILTER_TIMERS +" :include only events of type Timer. This can be used to produce the ration between\n" + 
							" throughput (i.e. calls into event entry points) and the number s of times timers\n" + 
							"set within them have exceeded their target thresholds\n" )	
	public String filter;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "whether to limit the rate to 100. The rate could be greater in case a logged error for example\n" + 
				"happens more than once per transaction as in the case of a retry loop.",
			defaultValue = "false")
	public boolean allowExcceed100;


}
