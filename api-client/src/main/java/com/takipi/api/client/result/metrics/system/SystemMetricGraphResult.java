package com.takipi.api.client.result.metrics.system;

import java.util.List;

import com.takipi.api.client.data.metrics.system.SystemMetricGraphPoint;
import com.takipi.api.core.result.intf.ApiResult;

public class SystemMetricGraphResult implements ApiResult {
	public List<SystemMetricGraphPoint> points;
}
