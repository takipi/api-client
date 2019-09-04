package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="functionList", type=FunctionType.Variable,
	description = "returns a list of all available functions", 
	example="", image="", isInternal=true)
public class FunctionListInput extends FunctionInput {
	
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DESCRIPTION = "description";
	public static final String EXAMPLE = "example";
	public static final String SCREENSHOT = "screenshot";
	public static final String INTERNAL = "internal";

	public static List<String> FILEDS = Arrays.asList(new String[] {
		NAME, TYPE, 	DESCRIPTION, EXAMPLE, SCREENSHOT, INTERNAL
	});
}
