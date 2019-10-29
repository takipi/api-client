package com.takipi.api.client.functions.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.takipi.api.client.util.regression.RegressionInput;
import com.takipi.common.util.CollectionUtil;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="regressions", type=FunctionType.Table,
description = " A function returning a table, graph or single stat relating to the number of new and increasing issues\n" + 
		"within a target set of events. The determination of which events have regressed is governed by the regression\n" + 
		"configuration in the Settings dashboard.", 
	example="Example query to return table:\n" + 
			"regressions({\"fields\":\"link,type,entry_point,introduced_by,severity,regression,\n" + 
			"typeMessage,error_location,stats.hits,rate,regDelta,first_seen,id\", \n" + 
			"\"render\":\"Grid\", \"regressionTypes\":\"Regressions,SevereRegressions\", \n" + 
			"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			"\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			"\"view\":\"$view\",\"maxColumnLength\":90,\"pointsWanted\":\"$pointsWanted\",\"\n" + 
			" types\":\"$type\",\"searchText\":\"$search\"})\n" + 
			"Example query for single stat:\n" + 
			"regressionReport({\"timeFilter\":\"$timeFilter\", \"environments\":\"$environments\",\n" + 
			"\"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			"\"view\":\"$view\",\"transactions\":\"$transactions\",\"pointsWanted\":\"$pointsWanted\",\n" + 
			"\"transactionPointsWanted\":\"$transactionPointsWanted\",\"types\":\"$type\",\n" + 
			"\"render\":\"SingleStat\"})", 
	image="https://drive.google.com/file/d/1n6CVDqhwr0cz6C1p0AbMtAkontVJ1M0P/view?usp=sharing," + 
			"https://drive.google.com/file/d/1PZHd27rVwt60pqcRIybgdYJ5qPSQQWB3/view?usp=sharing," + 
			"https://drive.google.com/file/d/1Nb1FEXTxoyXAjlwxXNfq8GkjsJYYYHTF/view?usp=sharing", isInternal=false)
public class RegressionsInput extends EventsInput {
	
	/**
	 * The max number of event tooltips to show on hover
	 */
	public static int MAX_TOOLTIP_ITEMS = 5;
	
	public static final String SEVERE_NEW_ISSUE_REGRESSIONS = "SevereNewIssues";
	public static final String NEW_ISSUE_REGRESSIONS = "NewIssues";
	public static final String SEVERE_INC_ERROR_REGRESSIONS = "SevereRegressions";
	public static final String INC_ERROR_REGRESSIONS = "Regressions";
	
	public static final List<String>REGRESSION_TYPES = Arrays.asList(new String[] {
			SEVERE_NEW_ISSUE_REGRESSIONS, NEW_ISSUE_REGRESSIONS, SEVERE_INC_ERROR_REGRESSIONS, INC_ERROR_REGRESSIONS});
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={SEVERE_NEW_ISSUE_REGRESSIONS, NEW_ISSUE_REGRESSIONS, SEVERE_INC_ERROR_REGRESSIONS, INC_ERROR_REGRESSIONS},
			defaultValue=INC_ERROR_REGRESSIONS,
			description = "Control the type of regressions returned by this graph. Default is Increasing / Regressed events.\n" + 
				SEVERE_NEW_ISSUE_REGRESSIONS + ": A severe new issue detected within the context of this regression report\n" +
				NEW_ISSUE_REGRESSIONS + ": A new issue detected within the context of this regression report\n" +
				SEVERE_INC_ERROR_REGRESSIONS + ": A severe regression detected within the context of this regression report\n" +
				INC_ERROR_REGRESSIONS +": A regression detected within the context of this regression report")	
	public String regressionTypes;
	
	public static final String SINGLE_STAT = "SingleStat";
	public static final String SINGLE_STAT_DESC = "SingleStatDesc";
	public static final String SINGLE_STAT_COUNT = "SingleStatCount";
	public static final String SINGLE_STAT_VOLUME = "SingleStatVolume";
	public static final String SINGLE_STAT_VOLUME_TEXT = "SingleStatVolumeText";
	public static final String GRID = "Grid";
	public static final String GRAPH = "Graph";
	public static final String FEED = "Feed";
	
	public static final List<String> SINGLE_STATS = Arrays.asList(new String[] {
		SINGLE_STAT, SINGLE_STAT_DESC, SINGLE_STAT_COUNT, SINGLE_STAT_VOLUME, SINGLE_STAT_VOLUME_TEXT});
	
	public static final String REGRESSIONS_SERIES = "regressions_series";

	/**
	 * Additional fields supported by this functions:
	 */
	
	/**
	 * one of the values in REGRESSION_TYPES
	 */
	public static final String REGRESSION_TYPE = "regression_type";
	
	/**
	 * A long form text description of the regression type: new, increasing, severe,...
	 */
	public static final String REG_DESC = "reg_desc";
	
	/**
	 * A long form  text description of a volume regression  rate change
	 */
	public static final String REGRESSION = "regression";
	
	/**
	 * A short form  text description of a volume regression rate change
	 */
	public static final String REG_DELTA = "reg_delta";
	
	/**
	 * outputs either SEVERITY_P1 or SEVERITY_P2
	 */
	public static final String SEVERITY = "severity";
	
	public static final int SEVERITY_P1 = 2;
	public static final int SEVERITY_P2 = 1;
	
	public static final List<String> REGRESSION_FIELDS;
	
	@Param(type=ParamType.Enum, advanced=false, 
			literals={SINGLE_STAT, SINGLE_STAT_DESC, SINGLE_STAT_COUNT, SINGLE_STAT_VOLUME,
					SINGLE_STAT_VOLUME_TEXT, GRID, GRAPH, FEED},
			defaultValue=GRID,
			description = "Control the type of output to return from the function\n" + 
				SINGLE_STAT + ": return a single stat value\n" +
				SINGLE_STAT_DESC + ": return a desc of the single stat value\n" +
				SINGLE_STAT_COUNT + ": return a the count of events returned\n" +
				SINGLE_STAT_VOLUME + ": return the volume of events returned\n" +
				SINGLE_STAT_VOLUME_TEXT + ": return a string value of the volume of events returned\n" +
				GRID + ": return a table with each row depicting a new or increasing error.\n" +
						"\tadditional available fields are:\n" + 
						"\t" + REG_DELTA + ": value of the regression rate" +
						"\t" + REGRESSION + ": description of the regression rate" +
						"\t" + REGRESSION + ": description of the regression" +
						"\t" + REG_DESC + ": Text value for the severity of the regression.\n" + 
								"\t\tIf the issue is severe = " + SEVERITY_P1 + "\n" + 
								"\t\tIf the issue is non-severe = " + SEVERITY_P2 + "\n" + 
								"\t\tIf the issue is not new or regressed = 0;" +
				GRAPH + ": This mode is only supported if the function is invoked to produce a reliabilty report,\n" + 
						" where each point depicts a key value from the report\n" +
				FEED +":")	
	
	public String render;
	
	public String getRenderMode() {
		
		if (render == null) {
			return GRID;
		}
		
		return render;
	}
	
	public boolean isSingleStat() {
		return SINGLE_STATS.contains(render);
	}
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = " An optional string format to be used when returning a single stat value. \n" + 
					" A decimal %d value will be passed into the string format",
			defaultValue = "")
	public String singleStatFormat;
	
	@Param(type=ParamType.Object, advanced=true, literals={},
		description = "An optional regressions objects to be used to programmatically pass regression inputs into the function." +
				"If no value is provided the settings for the env are used",
			defaultValue = "")
	public RegressionInput regressionInput;
	
	public boolean newOnly() {
		Collection<String> regressionTypes = getRegressionTypes();
		return newOnly(regressionTypes);
	}

	public static boolean newOnly(Collection<String> regressionTypes) {	
		
		if (CollectionUtil.safeIsEmpty(regressionTypes)) {
			return false;
		}
		
		boolean result = (!regressionTypes.contains(INC_ERROR_REGRESSIONS))
				&& (!regressionTypes.contains(SEVERE_INC_ERROR_REGRESSIONS));
		
		return result;
	}
	
	public Collection<String> getRegressionTypes() {
		
		if (regressionTypes == null) {
			return Collections.emptyList();
		}
		
		String[] parts = regressionTypes.split(ARRAY_SEPERATOR);
		Collection<String> result = new ArrayList<String>(parts.length);
		
		for (String part : parts) {
			
			if (REGRESSION_TYPES.contains(part)) {
				result.add(part);
			}
		}
		
		return result;
		
	}
	
	static {
		REGRESSION_FIELDS =  new ArrayList<String>(EventsInput.FIELDS);
		
		REGRESSION_FIELDS.addAll(Arrays.asList(new String[] {
				REGRESSION_TYPE, SEVERITY, REG_DELTA, REGRESSION, REG_DESC
		}));
	}
}
