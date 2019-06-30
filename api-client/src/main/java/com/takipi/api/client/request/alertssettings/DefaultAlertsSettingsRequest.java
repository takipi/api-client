package com.takipi.api.client.request.alertssettings;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.alertssettings.DefaultAlertsSettingsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class DefaultAlertsSettingsRequest extends ServiceRequest implements ApiGetRequest<DefaultAlertsSettingsResult> {
	protected DefaultAlertsSettingsRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<DefaultAlertsSettingsResult> resultClass() {
		return DefaultAlertsSettingsResult.class;
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

		public DefaultAlertsSettingsRequest build() {
			validate();

			return new DefaultAlertsSettingsRequest(serviceId);
		}
	}
}
