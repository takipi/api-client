package com.takipi.api.client.result.metrics;

import java.util.List;

import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.core.result.intf.ApiResult;

public class GraphResult implements ApiResult {
	public List<Graph> graphs;
}
