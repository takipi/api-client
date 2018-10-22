package com.takipi.api.client.data.transaction;

import java.util.List;

public class TransactionGraph {
	public String name;
	public List<GraphPoint> points;

	public static class GraphPoint {
		public String time;
		public Stats stats;
	}
}
