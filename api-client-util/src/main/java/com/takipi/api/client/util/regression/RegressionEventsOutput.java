package com.takipi.api.client.util.regression;

import java.util.Map;

import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.result.event.EventResult;

public class RegressionEventsOutput {
	public DeterminantKey determinantKey;

	public Graph baselineGraph;
	public Graph activeWindowGraph;

	public Map<String, EventResult> eventResultsMap;

	public RegressionEventsOutput(DeterminantKey determinantKey, Graph baselineGraph, Graph activeWindowGraph,
			Map<String, EventResult> eventResultsMap) {
		this.determinantKey = determinantKey;
		this.baselineGraph = baselineGraph;
		this.activeWindowGraph = activeWindowGraph;
		this.eventResultsMap = eventResultsMap;
	}
}
