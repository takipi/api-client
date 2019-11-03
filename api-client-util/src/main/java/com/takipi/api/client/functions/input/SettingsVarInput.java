package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="settingsVar", type=FunctionType.Variable,
description = " Used for internal purposes to retrieve a value from the Settings dashboard. ", 
example="", image="", isInternal=true)
public class SettingsVarInput extends BaseEnvironmentsInput {
	public String name;
	public String defaultValue;
	public boolean convertToArray;
}
