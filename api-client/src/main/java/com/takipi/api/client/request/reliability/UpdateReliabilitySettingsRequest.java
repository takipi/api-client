package com.takipi.api.client.request.reliability;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class UpdateReliabilitySettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String reliabilitySettingsJson;
	
	protected UpdateReliabilitySettingsRequest(String serviceId, String reliabilitySettingsJson) {
		super(serviceId);
		
		this.reliabilitySettingsJson = reliabilitySettingsJson;
	}
	
	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(1);
		
		map.put("reliability_settings_json", JsonUtil.stringify(reliabilitySettingsJson));
		
		return JsonUtil.createSimpleJson(map, false).getBytes(ApiConstants.UTF8_ENCODING);
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
			
			if (Strings.isNullOrEmpty(reliabilitySettingsJson)) {
				throw new IllegalArgumentException("Missing reliability settings json");
			}
		}
		
		public UpdateReliabilitySettingsRequest build() {
			validate();
			
			return new UpdateReliabilitySettingsRequest(serviceId, reliabilitySettingsJson);
		}
	}
}
