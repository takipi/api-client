package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.takipi.common.util.ArrayUtil;
import com.takipi.common.util.ObjectUtil;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 The base input for all template variable functions that operate on specific 
 * set of selected environments. 
 * 
*/

public abstract class BaseEnvironmentsInput extends VariableInput {

	@Param(type=ParamType.String, advanced=false, literals={},
		description = "A comma delimited array of environments names to use as a filter. \n" + 
			" Specify null, \"\", \"all\" or \"*\" to clear selection, in which case no environments are selected.\n",
		defaultValue = "$environments")
	public String environments;

	public static int MAX_COMBINE_SERVICES = 3;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
		description = "Control whether the information returned is limited to MAX_COMBINE_SERVICES," +
		" or allows the user to selected an unlimited number of envs to query.",
		defaultValue = "false")
	public boolean unlimited;
	
	public static List<String> getServiceIds(String environments) {
		if (!hasFilter(environments)) {
			return Collections.emptyList();
		}

		String[] serviceIds = ArrayUtil.safeSplitArray(environments, GRAFANA_SEPERATOR, false);
	
		if ((serviceIds.length == 1) && (serviceIds[0].equals("()"))) {
			return Collections.emptyList();
		}
		
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < serviceIds.length; i++) {
			
			String service = serviceIds[i];
			String value = service.replace("(", "").replace(")", "");
			
			String[] parts = value.split(SERVICE_SEPERATOR);

			String serviceId;
			
			if (parts.length > 1) {
				serviceId = parts[parts.length - 1];
			} else {
				serviceId = value;
			}
			
			if (service.startsWith(GRAFANA_VAR_PREFIX)) {
				continue;
			}
			
			result.add(serviceId);
		}

		return result;
	}
	
	public List<String> getServiceIds() {
		return getServiceIds(environments);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof BaseEnvironmentsInput)) {
			return false;
		}
		
		return ObjectUtil.equal(this.environments, ((BaseEnvironmentsInput)obj).environments);
	}
	
	@Override
	public int hashCode() {
		
		if (environments == null) {
			return super.hashCode();
		}
		
		return environments.hashCode();
	}
}
