package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;


@Function(name="deployments", type=FunctionType.Variable,
description = "This function returns the list of active deployments within the target environments.", 
	example="deployments({\"environments\":\"$environments\",\"sorted\":\"true\"})", 
	image="https://drive.google.com/file/d/10KZbc17klENm1eByEFxCTdCFXsMOC1CQ/view?usp=sharing", 
	isInternal=false)
public class DeploymentsInput extends BaseEnvironmentsInput {
	
}
