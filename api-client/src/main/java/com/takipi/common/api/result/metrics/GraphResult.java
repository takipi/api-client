package com.takipi.common.api.result.metrics;

import java.util.List;

import com.takipi.common.api.data.metrics.Graph;
import com.takipi.common.api.result.intf.ApiResult;

public class GraphResult implements ApiResult {
	public List<Graph> graphs;
}
