package com.takipi.common.api.result.metrics;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class GraphResult implements ApiResult {
	public List<Graph> graphs;

	public static class Graph {
		public String type;
		public String id;
		public List<GraphPoint> points;
	}

	public static class GraphPoint {
		public String time;
		public long value;
		public List<GraphPointContributor> contributors;
	}

	public static class GraphPointContributor {
		public String id;
		public long value;
	}
}
