package com.takipi.api.client.data.metrics;

import java.util.List;

import com.takipi.api.client.data.event.Stats;

public class Graph {
	public String type;
	public String id;
	public List<GraphPoint> points;
	
	public String machine_name;
	public String application_name;
	public String deployment_name;

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
