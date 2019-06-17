package com.takipi.api.client.util.regression;

import java.util.Map;
import java.util.Set;

import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.request.event.BreakdownType;
import com.takipi.api.client.result.event.EventResult;

public class RegressionEventsOutput {
	public DeterminantKey determinantKey;

	public Graph baselineGraph;
	public Graph activeWindowGraph;

	public Map<String, EventResult> eventResultsMap;
	public Set<BreakdownType> breakdownTypes;
	
	public RegressionEventsOutput(DeterminantKey determinantKey, Graph baselineGraph, Graph activeWindowGraph,
			Map<String, EventResult> eventResultsMap, Set<BreakdownType> breakdownTypes) {
		this.determinantKey = determinantKey;
		this.baselineGraph = baselineGraph;
		this.activeWindowGraph = activeWindowGraph;
		this.eventResultsMap = eventResultsMap;
		this.breakdownTypes = breakdownTypes;
	}
}
