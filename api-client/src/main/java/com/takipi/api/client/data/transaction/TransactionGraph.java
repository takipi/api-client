package com.takipi.api.client.data.transaction;

import java.util.List;

public class TransactionGraph {
	public String name;
	public String class_name;
	public String method_name;
	public String method_desc;

	public List<GraphPoint> points;

	public static class GraphPoint {
		public String time;
		public Stats stats;
	}
}
