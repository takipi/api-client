package com.takipi.api.client.request.metrics.system;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.metrics.system.SystemMetricMetadatasResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class SystemMetricMetadatasRequest extends ServiceRequest implements ApiGetRequest<SystemMetricMetadatasResult> {
	SystemMetricMetadatasRequest(String serviceId) {
		super(serviceId);
	}
	
	@Override
	public Class<SystemMetricMetadatasResult> resultClass() {
		return SystemMetricMetadatasResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/metrics/system";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public SystemMetricMetadatasRequest build() {
			validate();
			
			return new SystemMetricMetadatasRequest(serviceId);
		}
	}
}
