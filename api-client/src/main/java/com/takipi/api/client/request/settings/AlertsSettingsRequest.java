package com.takipi.api.client.request.settings;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Strings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.settings.AlertsSettingsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class AlertsSettingsRequest extends ServiceRequest implements ApiGetRequest<AlertsSettingsResult> {
	public final String email;

	protected AlertsSettingsRequest(String serviceId, String email) {
		super(serviceId);

		this.email = email;
	}

	@Override
	public Class<AlertsSettingsResult> resultClass() {
		return AlertsSettingsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/alerts";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		if (Strings.isNullOrEmpty(email)) {
			return null;
		}

		String[] params = new String[1];

		params[0] = "email=" + encode(email);

		return params;
	}

	public static class Builder extends ServiceRequest.Builder {
		private String email;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;

			return this;
		}

		public AlertsSettingsRequest build() {
			validate();

			return new AlertsSettingsRequest(serviceId, email);
		}
	}
}
