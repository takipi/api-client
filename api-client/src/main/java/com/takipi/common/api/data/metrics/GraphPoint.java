package com.takipi.common.api.data.metrics;

import java.util.List;

public class GraphPoint {
	public String time;
	public long value;
	public List<GraphPointContributor> contributors;
}
