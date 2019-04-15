package com.takipi.api.client.result.metrics.system;

import com.takipi.api.client.data.metrics.system.SystemMetricMetadataUnit;
import com.takipi.api.core.result.intf.ApiResult;

public class SystemMetricMetadataResult implements ApiResult {
	public String service_id;
	public String name;
	public String display_name;
	public SystemMetricMetadataUnit unit;
}
