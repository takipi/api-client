package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="systemMetricsMetadata", type=FunctionType.Variable,
description = "This function retreives all available collected system metrics names", 
example="", image="", isInternal=true)
public class SystemMetricsMetadataInput extends BaseEnvironmentsInput {
	
	public static final String[] PRIO_SYSTEM_METRICS = new String[] {
		"class-loading-total-loaded-classes",
		"threads-count",
		"threads-count-waiting"
	};
	
	public boolean addNone;
}
