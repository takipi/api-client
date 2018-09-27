package com.takipi.common.api.data.metrics;

import java.util.List;

import com.takipi.common.api.data.event.Stats;

public class Graph {
	public String type;
	public String id;
	public List<GraphPoint> points;

	public static class GraphPoint {
		public String time;
		public Stats stats;
		public List<GraphPointContributor> contributors;
	}

	public static class GraphPointContributor {
		public String id;
		public Stats stats;
	}
}
