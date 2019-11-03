package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="slowTransactions", type=FunctionType.Variable,
description = "This function populates a template variable holding a list of transactions whose performance state matches that of\n" + 
		" a target set of performance states. ", 
	example="slowTransactions({\"environments\":\"$environments\",\"view\":\"All Events\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"sorted\":\"true\", \n" + 
			"\"pointsWanted\":\"$transactionPointsWanted\", \"performanceStates\":\"SLOWING|CRITICAL\"})", 
	image=" https://drive.google.com/file/d/1FblzDqI5NeMA9Zt1rNdMDE36132kMCDR/view?usp=sharing", 
	isInternal=false)
public class SlowTransactionsInput extends BaseEventVolumeInput {
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={CRITICAL, SLOWING, OK},
			defaultValue=CRITICAL,
			description = "A | delimited array of performance states, that a target transaction must meet in order to be returned\n" + 
					"by this function. Possible values are:"  +
				CRITICAL + ": The transaction has slown down\n" +
				SLOWING +": The transaction is in the process of slowning down\n" +
				OK + ": The tranaction performance is OK\n")
	public String performanceStates;
}
