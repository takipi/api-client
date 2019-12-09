package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="featureFields", type=FunctionType.Variable,
description = "", example="", image="", isInternal=true)
public class FeatureFieldsInput extends VariableInput{
	
	public String featureName;
	public String fieldName;
	
	public static final String  NAME = "name";
	public static final String  TITLE = "title";
	public static final String  SUB_TITLE= "sub_title";
	public static final String  TEXT = "text";
	public static final String  VIDEO_URL = "video_url";
	public static final String  SCREENSHOT_URL = "screenshot_url";
	public static final String  DEMO_URL = "demo_url";
	public static final String  INSTALL_URL= "install_url";
	
	public static List<String> FIELDS = Arrays.asList(new String[] {
		NAME	, TITLE, SUB_TITLE, TEXT, SCREENSHOT_URL, VIDEO_URL, DEMO_URL,INSTALL_URL 	
	});
}
