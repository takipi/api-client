package com.takipi.api.client.request.alertssettings;

import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GetAlertsSettingsRequest extends ServiceRequest implements ApiGetRequest<DefaultAlertsSettings> {
	protected GetAlertsSettingsRequest(String serviceId) {
		super(serviceId);
	}
	
	@Override
	public Class<DefaultAlertsSettings> resultClass() {
		return DefaultAlertsSettings.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/alerts";
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
		
		public GetAlertsSettingsRequest build() {
			validate();
			
			return new GetAlertsSettingsRequest(serviceId);
		}
	}
}
