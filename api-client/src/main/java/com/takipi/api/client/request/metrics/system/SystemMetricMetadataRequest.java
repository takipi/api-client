package com.takipi.api.client.request.metrics.system;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.metrics.system.SystemMetricMetadataResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class SystemMetricMetadataRequest extends ServiceRequest implements ApiGetRequest<SystemMetricMetadataResult> {
	public final String metricName;
	
	private SystemMetricMetadataRequest(String serviceId, String metricName) {
		super(serviceId);
		
		this.metricName = metricName;
	}
	
	@Override
	public Class<SystemMetricMetadataResult> resultClass() {
		return SystemMetricMetadataResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/metrics/system/" + metricName;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private String metricName;
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder setMetricName(String metricName) {
			this.metricName = metricName;
			
			return this;
		}
		
		public SystemMetricMetadataRequest build() {
			validate();
			
			return new SystemMetricMetadataRequest(serviceId, metricName);
		}
	}
}
