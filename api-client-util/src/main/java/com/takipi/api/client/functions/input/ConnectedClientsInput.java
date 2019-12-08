package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="connectedClients", type=FunctionType.Table,
description = "This function outputs a report of the application to which an OverOps agent is attached\n" + 
		" with the target environment(s). Fields returned are:", 
	example="connectedClients({\"environments\":\"$environments\",\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\"," +
		  "\"outputMode\":\"Grid\" \"searchText\":\"$search\"})",
	image="", isInternal=false)
public class ConnectedClientsInput extends EnvironmentsFilterInput {
	
	public static final String SingleStat = "SingleStat";
	public static final String Grid = "Grid";

	@Param(type=ParamType.Enum, advanced=false, literals={SingleStat,Grid},
			description = "The type of output returned by this function: grid or number of processes\n " +
			SingleStat + " = Output the number of connected procsses\n" + 
			Grid + " = Output the details of the connected procsses in list format\n",
			defaultValue = Grid)
	public String outputMode;
	
	public String getOutputMode() {
		
		if (outputMode == null) {
			return Grid;
		}
		
		return outputMode;
	}
		
	@Param(type=ParamType.String, advanced=false, literals={},
			description = " A string used to filter app, server, dep name by",
			defaultValue = "")
	public String searchText;
	
	/**
	 * The name of the field marking the name of the app to which the connected processes belong
	 */
	public static final String APP_NAME = "app";
	

	/**
	 * The name of the field marking the name of the server group to which the connected processes belong
	 */
	public static final String MACHINE_NAME = "server_group";
	
	/**
	 * The name of the field marking the name of the deployment to which the connected processes belong
	 */
	public static final String DEPLOYMENT_NAME = "deployment";

	/**
	 * A comma delimited list of the pids of connected processes with the server, app, server and deployment tags 
	 */
	public static final String PIDS_NAME = "pids";

	/**
	 * The list of fields returned by this function
	 */
	public static final List<String> FIELDS = Arrays.asList(new String[] {APP_NAME, 
		DEPLOYMENT_NAME, MACHINE_NAME, PIDS_NAME});	
}
