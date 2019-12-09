package com.takipi.api.client.functions.input;

import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="reliabilityKpiGraph,ReliabilityKpiGraph", type=FunctionType.Table,
description = " A function charting a target reliability KPI for a selected set of apps. KPIs include:\n" + 
		"reliability score, error volumes, new errors, increasing errors and more. The out put\n" + 
		"can be set to chart results over time, grouping them by set report intervals (e.g. 1d, 7d,..)\n" + 
		"or produce a single average value for each scored application.", 
	example="ReliabilityKpiGraph({\"volumeType\":\"all\",\"view\":\"$view\",\n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \n" + 
			"\"applications\":\"$applications\", \"servers\":\"$servers\",\n" + 
			"\"types\":\"$type\",\"limit\":\"$limit\", \n" + 
			"\"reportInterval\":\"24h\", \"kpi\":\"$kpi\"}) ", 
	image="Screenshot: https://drive.google.com/file/d/1kSUO1SE5gOqVCYb5eKrsWYxi4PJNbs9J/view?usp=sharing", isInternal=false)
public class ReliabilityKpiGraphInput extends GraphInput {
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = " The kpi to chart: \n" + ReliabilityReportInput.KPIS_DESC,
			defaultValue = "")
	public String kpi;
	
	@Param(type=ParamType.Number, advanced=false, literals={},
			description = "The max number of apps to chart",
			defaultValue = "0")
	public int limit;
	

	@Param(type=ParamType.Enum, advanced=false, literals={Day, Week, Hour},
			description = "The interval by which to group data (e.g. 1d, 7d,..)",
			defaultValue = Day)
	public String reportInterval;
	
	public String getReportInterval() {
		
		if ((reportInterval == null) || (reportInterval.isEmpty())) {
			return Day;
		}
		
		return reportInterval;
	}
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Control whether to aggregate all apps kpis, or break down by the report interval\n" + 
					"for trends over time",
			defaultValue = "false")
	public boolean aggregate;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={},
			description = "Control whether too chart scores as 100 - <score>",
			defaultValue = "false")
	public boolean deductFrom100;
	
}
