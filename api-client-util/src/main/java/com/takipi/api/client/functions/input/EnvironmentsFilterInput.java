package com.takipi.api.client.functions.input;

import com.google.common.base.Objects;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * Input for functions that use a filter to request data from a specific combination
 * of applications, deployments and / or server groups.
 */
public abstract class EnvironmentsFilterInput extends BaseEnvironmentsInput {
	
	public static final String APP_LABEL_PREFIX = "--";
	
	public static final String DEFAULT_APPS = "$applications";
	public static final String DEFAULT_DEPS = "$deployments";
	public static final String DEFAULT_SERVERS = "$servers";

	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue=DEFAULT_APPS,
		description = "A comma delimited array of application names to use as a filter. Specify \"\", \"all\" or \"*\" to skip\n")
	public String applications;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue=DEFAULT_SERVERS,
		description = "A comma delimited array of server group names to use as a filter. Specify \"\", \"all\" or \"*\" to skip\n")		
	public String servers;
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue=DEFAULT_DEPS,
			description = "A comma delimited array of deployments names to use as a filter. Specify \"\", \"all\" or \"*\" to skip\n")	
	public String deployments;
	
	public boolean hasApplications() {
		return hasFilter(applications);
	}

	public boolean hasServers() {
		return hasFilter(servers);
	}

	public boolean hasDeployments() {
		return hasFilter(deployments);
	}
	
	public boolean hasDeterminantFilter() { 
		boolean result = hasApplications() || hasDeployments() || hasServers(); 
		return result;
	}
	
	public static String toAppLabel(String app) {
		return APP_LABEL_PREFIX + app;
	}
	
	public static boolean isLabelApp(String app) {
		return app.startsWith(EnvironmentsFilterInput.APP_LABEL_PREFIX);
	}
	
	public static String getLabelAppName(String value) {
		
		if ((value == null) || (!isLabelApp(value))) {
			return value;
		}

		return value.substring(EnvironmentsFilterInput.APP_LABEL_PREFIX.length());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!super.equals(obj)) {
			return false;
		}
		
		if (!(obj instanceof EnvironmentsFilterInput)) {
			return false;
		}
		
		EnvironmentsFilterInput other = (EnvironmentsFilterInput)obj;
		
		return Objects.equal(applications, other.applications) 
				&& Objects.equal(deployments, other.deployments)
				&& Objects.equal(servers, other.servers);
	}
	
}
