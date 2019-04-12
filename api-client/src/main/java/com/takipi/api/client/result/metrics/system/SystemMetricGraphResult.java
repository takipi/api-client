package com.takipi.api.client.result.metrics.system;

import java.util.List;

import com.takipi.api.core.result.intf.ApiResult;

public class SystemMetricGraphResult implements ApiResult {
	public List<SystemMetricGraphPoint> points;
	
	public static class SystemMetricGraphPoint {
		public String time;
		public double value;
	}
}
