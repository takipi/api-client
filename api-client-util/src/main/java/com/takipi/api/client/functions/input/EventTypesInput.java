package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.takipi.common.util.ArrayUtil;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="eventTypes", type=FunctionType.Variable,
description = "This function populates a variable containing all the event types available to the user to filter \n" + 
		" events by. The functions returns a list containing three types of event categorizations:\n" + 
		" 1. A list of all event types available to the user to filter by. These could be provided by the function's\n" + 
		" types variable, or if left null will be read from the \"event_types\" value under the \"general\"\n" + 
		" section of the Settings dashboard.\n" + 
		" 2. A list of all code tiers to which events active in the last 7 days belong. These are preceded \n" + 
		" by a \"-\" prefix.\n" + 
		" 3. A list of all exception simple name (e.g. NullPointerException) that match those of events\n" + 
		" active in the last 7 days. These values are preceded by a \"--\" prefix.\n", 
	example="eventTypes({\"environments\":\"$environments\", \"view\":\"$view\"}) ", 
	image="https://drive.google.com/file/d/1bCrsjOcPZrht7Z78qi4XRHr3O4xF7CoF/view?usp=sharing", isInternal=false)

public class EventTypesInput extends ViewInput {
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "A | delimited list of the events type to add to the list. If no value is provided,\n" + 
			" the event_types property is used from the Settings dashboard. ")
	public String types; 
	
	@Param(type=ParamType.Boolean, advanced=false, literals={}, defaultValue="false",
			description = "if set to true only populate values seen in events first seen in the last week")
	public boolean newOnly;

	public static final String EventTypes = "EventTypes";
	public static final String ExceptionTypes = "ExceptionTypes";
	public static final String Tiers = "Tiers";

	/**
	 * The different event types that will be added to the list
	 */
	
	@Param(type=ParamType.Enum, advanced=false, literals={EventTypes, ExceptionTypes, Tiers}, defaultValue="all",
			description = "A comma delimited list of the different event types to populate in the list. If no value is specified,\n" + 
				"all event types are added:\n" + 
				EventTypes + ": Populate the different available event types\n" +
				ExceptionTypes + ": Populate the different exception types available to the usert\n" +
				Tiers + ": Populate the different exception types available to the user")
	public String eventTypes;
	
	public Collection<String> getTypes() {
		
		if ((types == null) || (types.isEmpty())) {
			return Collections.emptyList();
		}

		return Arrays.asList(ArrayUtil.safeSplitArray(types, GRAFANA_SEPERATOR, false));
	}
	
	private static final List<String> EVENT_TYPES = Arrays.asList(new String[] {EventTypes, ExceptionTypes, Tiers});
	
	public Collection<String> getEventTypes() {
		
		if ((eventTypes == null) || (eventTypes.isEmpty())) {
			return EVENT_TYPES;
		}
		
		String[] parts = eventTypes.split(ARRAY_SEPERATOR);
		Collection<String> result = new ArrayList<String>(parts.length);
		
		for (String part : parts) {
				
			if (EVENT_TYPES.contains(part)) {
				result.add(part);
			}
		}
		
		return result;
		
	}
}

