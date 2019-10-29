package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;


@Function(name="increasingErrors", type=FunctionType.Variable,
description = "Populates a variable containing all code locations (class.method) which have experience\n" + 
		" an increase in the target time frame. The function uses a regression function internally to\n" + 
		" alculate which events have increased in volume.", 
	example= "", image= "", isInternal=false)
public class RegressedEventsInput extends EventsInput {
	
}
