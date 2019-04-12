package com.takipi.api.client.result.metrics.system;

import com.takipi.api.core.result.intf.ApiResult;

public class SystemMetricMetadataResult implements ApiResult {
	public String service_id;
	public String name;
	public String display_name;
	public SystemMetricMetadataUnit unit;
	
	public static class SystemMetricMetadataUnit {
		public String type;
		public String suffix;
		public String plural_suffix;
	}
}
