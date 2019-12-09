package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="functionParams", type=FunctionType.Variable,
	description = "Returns a list of all the params for the target function. ", 
	example="", image="", isInternal=true)
public class FunctionParamsInput extends FunctionInput {
	
	public static final String FUNCTION_NAME = "function_name";
	public static final String PARAM_NAME = "param_name";
	public static final String TYPE = "type";
	public static final String DESCRIPTION = "description";
	public static final String LITERALS = "literals";
	public static final String ADVANCED = "advanced";

	public static List<String> FILEDS = Arrays.asList(new String[] {
		FUNCTION_NAME, PARAM_NAME, TYPE, DESCRIPTION, LITERALS, ADVANCED
	});
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="false",
		description = "name of the function for which to get available params. " +
			"This could be any of the function names provided by the functionList function.") 
	public String functionName;
}
