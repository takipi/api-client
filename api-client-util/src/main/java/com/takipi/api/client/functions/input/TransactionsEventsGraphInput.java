package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="transactionsEventsGraph", type=FunctionType.Graph,
	description = "This function charts the volume of errors for the selected group" +
			"of transactions. If no transactions are chosen, selected event types" +
			"will be charted. If no event types are selected, the volume of all events" +
			"within the filter will be charted", 
	example="", 
	image="", isInternal=false)
public class TransactionsEventsGraphInput extends GraphLimitInput {
	
}
