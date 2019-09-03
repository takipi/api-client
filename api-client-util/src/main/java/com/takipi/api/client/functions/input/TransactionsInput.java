package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="transactions", type=FunctionType.Variable,
description = "A function returning a list of transaction available for a user to filter target events by.\n" + 
		"If any key transaction groups are defined in the Settings dashboard are defined they are returned,\n" + 
		"followed by a list of all transaction (entry point) names of events active within the provided time frame.", 
	example="transactions({\"environments\":\"$environments\",\"view\":\"All Events\",\n" + 
			"\"timeFilter\":\"time >= now() - 7d\",\"sorted\":\"true\"})", 
	image="https://drive.google.com/open?id=1y_AneI6seWBF2GQxZDNXFYFB7Ku-0LBv", 
	isInternal=false)
public class TransactionsInput extends BaseEventVolumeInput {
	
}
