package com.takipi.api.client.request.advanced;

import com.takipi.api.client.data.advanced.AdvancedSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GetAdvancedSettingsRequest extends ServiceRequest
		implements ApiGetRequest<AdvancedSettings> {
	protected GetAdvancedSettingsRequest(String serviceId) {
		super(serviceId);
	}
	
	@Override
	public Class<AdvancedSettings> resultClass() {
		return AdvancedSettings.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/advanced-settings";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public GetAdvancedSettingsRequest build() {
			validate();
			
			return new GetAdvancedSettingsRequest(serviceId);
		}
	}
}
