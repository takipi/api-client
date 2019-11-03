package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;


@Function(name="environments", type=FunctionType.Variable,
description = "As the input for the environments functions that is used to populate a template variable \n" + 
		" containing the names of all environments to which the OO API key provided to the Grafana datasource \n" + 
		" connected to this query has access to. \n", 
	example="environments({\"sorted\":\"true\"})", 
	image="https://drive.google.com/file/d/187V1IuD5PeC9cz9sd4nzpY12q2PxpmW9/view?usp=sharing", 
	isInternal=false)
public class EnvironmentsInput extends BaseEnvironmentsInput {
}
