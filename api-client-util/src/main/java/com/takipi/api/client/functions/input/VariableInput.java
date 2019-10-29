package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.takipi.common.util.ArrayUtil;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * The base function input for all functions that are used to populate template variables:
 * http://docs.grafana.org/reference/templating/ 
 */
public abstract class VariableInput extends FunctionInput {
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Control whether values of this variable are sorted alphabetically or logically",
			defaultValue = "false")
	public boolean sorted;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Controls whether values are added as separate items, or as one comma delimited value",
			defaultValue = "false")
	public boolean commaDelimited;
	
	public static boolean hasFilter(String value) {
		
		return (value != null)
			&& (value.length() != 0) 
			&& (!VAR_ALL.contains(value));
	}
	
	public static Collection<String> getServiceFilters(String value, String serviceId, boolean matchCase) {

		if (!hasFilter(value)) {
			return Collections.emptyList();
		}
		
		String target;
				
		if ((value.startsWith("(")) && (value.endsWith(")"))) {
			target = value.substring(1, value.length() - 1);
		} else {
			target = value;
		}

		String[] parts = ArrayUtil.safeSplitArray(target, GRAFANA_SEPERATOR, false);
		Set<String> result = new HashSet<String>();

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			
			if (part.length() == 0) {
				continue;
			}

			if (!matchCase) {
				part = part.toLowerCase();
			}

			String[] subParts = part.split(SERVICE_SEPERATOR);

			if ((serviceId != null) && (subParts.length > 1)) {
				if (!serviceId.equals(subParts[1])) {
					continue;
				}

				result.add(subParts[0]);
			} else {
				result.add(part);
			}
		}

		return new ArrayList<String>(result);
	}
}
