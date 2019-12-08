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
	
	public static final String TIME_FILTER_SELECTION = "timeFilter";
	public static final String TIME_FILTER_DEPLOYMENT_TIMESPAN = "invocations";
	
	@Param(type=ParamType.Enum, advanced=false, 
		literals= {TIME_FILTER_SELECTION, TIME_FILTER_DEPLOYMENT_TIMESPAN},
		defaultValue=TIME_FILTER_SELECTION,
		description = "Control which timeframe is used by this function for querying data:\n" +
				TIME_FILTER_SELECTION + ": use the provided timeFilter when querying data\n" + 
				TIME_FILTER_DEPLOYMENT_TIMESPAN +": if deployment filters are provided use their timespan when quering data\n") 
	public String timeFilterMode;
	
	public boolean adjustToDepTimespan() {
		return (timeFilterMode == null) 
			|| (Objects.equal(timeFilterMode, TIME_FILTER_DEPLOYMENT_TIMESPAN));
	}
	
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
