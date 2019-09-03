package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="apiHost", type=FunctionType.Variable,
description = "This function provides a way to link a widget to the OverOps backend server represented by  \n" + 
	" the query's data source, either on-premises or SaaS. This can be used to link a widget to an OverOps ARC link\n" + 
	" or dashboard view.\n" +  
	" The function can be used to retrieve either the backend URL, port or combination of both\n" + 
	" depending on the value of the \"type\" value", 
	example="apiHost(\"type\":\"PORT\"}", 
	image="", isInternal=false)
public class ApiHostInput extends VariableInput {
			
	public static final String FullURL = "FullURL";
	public static final String URL = "URL";
	public static final String API_URL = "API_URL";
	public static final String PORT = "PORT";
	public static final String URL_NO_PORT = "URL_NO_PORT";
	public static final String API_VER = "API_VER";
	
	@Param(type=ParamType.Enum, advanced=false, 
		literals={FullURL, URL, API_URL, PORT, URL_NO_PORT, API_VER},
		defaultValue=FullURL,
		description = "The URL type to return as one of the HostType options \n" +
		FullURL + ": the complete URL to the OverOps backend including port and protocol (i.e. https://app.overops.com:443)\n" +
		URL +": the complete URL to the OverOps backend including port (i.e. app.overops.com:443\n" +
		API_URL + ": the complete URL to the OverOps backend REST endpoint including port (i.e. api.overops.com:443)\n" +
		PORT + ": the port number (i.e. app.overops.com\n" +
		URL_NO_PORT + ": the URL without a port value  (i.e 443)\n" +
		API_VER  + ": REST api version: (e.g. 1)" 
	)

	public String type;
}
