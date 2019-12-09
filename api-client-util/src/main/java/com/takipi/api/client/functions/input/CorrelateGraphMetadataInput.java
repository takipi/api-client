package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;

@Function(name="correlateMetadata", type=FunctionType.Variable,
description = "This function retreives all available collected system metrics names + througput", 
example="", image="", isInternal=true)
public class CorrelateGraphMetadataInput extends SystemMetricsMetadataInput {
	
}
