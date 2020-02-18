package com.takipi.api.client.request.reliability;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class UpdateReliabilitySettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String reliabilitySettingsJson;

	protected UpdateReliabilitySettingsRequest(String serviceId, String reliabilitySettingsJson) {
		super(serviceId);

		this.reliabilitySettingsJson = reliabilitySettingsJson;
	}

	@Override
	public String postData() {
		return JsonUtil.createSimpleJson(CollectionUtil.mapOf("reliability_settings_json", reliabilitySettingsJson), true);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/reliability-settings";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String reliabilitySettingsJson;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setReliabilitySettingsJson(String reliabilitySettingsJson) {
			this.reliabilitySettingsJson = reliabilitySettingsJson;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (StringUtil.isNullOrEmpty(reliabilitySettingsJson)) {
				throw new IllegalArgumentException("Missing reliability settings json");
			}

			if (!JsonUtil.isLegalJson(reliabilitySettingsJson)) {
				throw new IllegalArgumentException("Reliability settings is in illegal json format");
			}
		}

		public UpdateReliabilitySettingsRequest build() {
			validate();

			return new UpdateReliabilitySettingsRequest(serviceId, reliabilitySettingsJson);
		}
	}
}
