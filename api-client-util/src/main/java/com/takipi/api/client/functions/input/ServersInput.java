package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="servers", type=FunctionType.Variable,
description = "This function returns the list of active servers within the target environments.", 
	example="servers({\"environments\":\"$environments\",\"sorted\":\"true\"})", 
	image="https://drive.google.com/file/d/1nR2HMzxTyL3NhA6Ss70pFnVO1SAYQ15A/view?usp=sharing", 
	isInternal=false)

public class ServersInput extends BaseEnvironmentsInput {
	
}
