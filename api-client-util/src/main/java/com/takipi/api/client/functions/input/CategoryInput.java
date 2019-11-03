package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="category", type=FunctionType.Graph,
description = "This function returns Graph series for each of the views held within a target category",
	example=
	"\"category({\"graphType\":\"view\",\"volumeType\":\"all\",\"view\":\"$view\"," +
		"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\"," +
		"\"servers\":\"$servers\", \"deployments\":\"$deployments\",\"pointsWanted\":\"$pointsWanted" +
		"\"types\":\"$type\",\"seriesName\":\"Times\", \"transactions\":\"$transactions\"" +
		"\"searchText\":\"$search\", \"category\":\"Apps\", \"limit\":5})",
	image="", isInternal=false)
public class CategoryInput extends GraphInput {
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "The category name from which to produce graphs",
			defaultValue = "")
	public String category;
		
	@Param(type=ParamType.Number, advanced=false, literals={},
			description = "Limit the number of graphs returned by this function. The views chosen by the function" +
					 " are those with the most volume of events within the category. For example, by setting this" + 
					 " value to 3, the 3 views with the most volume of events within the category will be returned.", 
			defaultValue = "0")
	public int limit;
}
