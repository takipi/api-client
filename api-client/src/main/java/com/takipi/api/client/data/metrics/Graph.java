package com.takipi.api.client.data.metrics;

import java.util.ArrayList;
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

		@Override
		public GraphPoint clone() {
			GraphPoint graphPoint = new GraphPoint();

			graphPoint.time = this.time;

			if (stats != null) {
				graphPoint.stats = this.stats.clone();
			}

			if (contributors != null) {
				graphPoint.contributors = new ArrayList<>();

				for (GraphPointContributor graphPointContributor : this.contributors) {
					if (graphPointContributor != null) {
						graphPoint.contributors.add(graphPointContributor.clone());
					}
				}
			}

			return graphPoint;
		}
	}

	public static class GraphPointContributor {
		public String id;
		public Stats stats;

		@Override
		public GraphPointContributor clone() {
			GraphPointContributor graphPointContributor = new GraphPointContributor();

			graphPointContributor.id = id;

			if (stats != null) {
				graphPointContributor.stats = stats.clone();
			}

			return graphPointContributor;
		}
	}
}
