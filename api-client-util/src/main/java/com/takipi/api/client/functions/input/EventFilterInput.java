package com.takipi.api.client.functions.input;

import java.util.Collection;
import java.util.Collections;

import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * The base function input used to include / exclude event objects matching a specific criteria
 * the could be selected by the user. 
 *
 */
public abstract class EventFilterInput extends ViewInput {
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "An optional comma delimited string array of deployment names to select all objects introduced by \n" + 
				"a target set of deployments", 
			defaultValue = "")
	public String introducedBy;
		
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional comma delimited array of type identifier to select all object of a specific type\n" + 
			"	Each value can be one of the following :\n" + 
			"	1.The type name of the object. Possible values include: \"Logged Error, \"Logged Warning,\n" + 
			"	2. \"Uncaught Exception, Caught Exception, \"Swallowed Exception\", \"Timer\". \n" + 
			"	The simple name of an exception class. Must be preceded by a double dash ('--'). \n" + 
			"	3. The name of a category as defined in https://git.io/fpPT0, or as added via the Settings dashboard.\n" + 
			"	Must be preceded by a dash ('-').\n" + 
			"	The logical relationship between the values in the types array is a logical AND which means\n" + 
			"	an object must pass all of the selectors to specified to be selected. \n" ,
 
			defaultValue = "")
	public String types;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
				"An optional comma delimited list of labels names the event must match to be selected. It is " +
				"enough for the event to match at least one of the labels specified to be selected",
			defaultValue = "")
	public String labels;

	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional string value list containing a value in the format of class.method.\n" + 
			 "If the value is matched against the error_location of a target event, it is selected.",
			defaultValue = "")
	public String eventLocations;

	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional regex pattern applied to each of the target event's labels. If one of the labels " + 
			"match the regex pattern the event is selected.",
			defaultValue = "")
	public String labelsRegex;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional String value containing a ISO 8601 date time value. For the object to be selected " + 
			"its first_seen attribute must be later than the date specified.",
			defaultValue = "")
	public String firstSeen;
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional string value containing a term that must be contained in a target event's " + 
			"error location (class or method name), entry point (class or method name), message or type for it to be selected. The value '<term>' is ignored.",  
			defaultValue = "")
	public String searchText;
		
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
				"An optional list of event types (as specified by the types array, excluding exception names and categories) " +
				"that a target event must match to be selected. This filter is applied as a logical AND to " +
				"that specified by the types filter.",
			defaultValue = "")
	public String allowedTypes;
	
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description =
			"An optional string value that must be contained in the event's entry point (class or name) for it to be selected.",
			defaultValue = "")
	public String transactionSearchText;
	
	public String getSearchText() {
		
		if ((searchText == null) || (searchText.equals(TERM)) ) {
			return null;
		}
		
		return searchText;
		
	}
	
	public boolean hasEventFilter() {
		
		if ((introducedBy != null) && (introducedBy.length() > 0)) {
			return true;
		}
		
		if ((types != null) && (types.length() > 0)) {
			return true;
		}
		
		if ((labels != null) && (labels.length() > 0)) {
			return true;
		}
		
		if ((labelsRegex != null) && (labelsRegex.length() > 0)) {
			return true;
		}
		
		if ((firstSeen != null) && (firstSeen.length() > 0)) {
			return true;
		}
		
		if (hasTransactions()) {
			return true;
		}
		
		return false;
	}
	
	public boolean hasIntroducedBy() {
		return hasFilter(introducedBy);
	}
	
	public Collection<String> getIntroducedBy(String serviceId) {
		
		if (introducedBy == null) {
			return Collections.emptySet();
		}
		
		return getServiceFilters(introducedBy, serviceId, true);
	}
		
	public Collection<String> geLabels(String serviceId) {
		
		if (labels == null) {
			return Collections.emptySet();
		}
		
		return getServiceFilters(labels, serviceId, true);
	}	
}
