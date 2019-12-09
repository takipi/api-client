package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 *	The base input for all graph function that limit the number of time series returned by the function.
 *	This could be effective, where a possible graph function can return a large number of series (for example
 *	a category graph that holds many views), and there is a need to limit based on certain criteria (e.g. which
 *	series has more volume) the number of actual graphs presented to the user.
 *
 */

public abstract class GraphLimitInput extends GraphInput {
	
	@Param(type=ParamType.Number, advanced=false, literals={}, defaultValue="0",
			description = "The max number of time series to be returned by a query invoking this function.") 
	public int limit;
}
