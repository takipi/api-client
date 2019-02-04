package com.takipi.api.client.request.advanced;

import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.takipi.api.client.data.advanced.AdvancedSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;

public class UpdateAdvancedSettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final AdvancedSettings advancedSettings;
	
	protected UpdateAdvancedSettingsRequest(String serviceId, AdvancedSettings advancedSettings)
	{
		super(serviceId);
		
		this.advancedSettings = advancedSettings;
	}
	
	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		return (new Gson()).toJson(this.advancedSettings).getBytes(ApiConstants.UTF8_ENCODING);
	}
	
	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/advanced-settings";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private AdvancedSettings advancedSettings;
		
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder setAdvancedSettingsJson(AdvancedSettings advancedSettings) {
			this.advancedSettings = advancedSettings;
			
			return this;
		}
		
		@Override
		protected void validate() {
			super.validate();
		}
		
		public UpdateAdvancedSettingsRequest build() {
			validate();
			
			return new UpdateAdvancedSettingsRequest(serviceId, advancedSettings);
		}
	}
}
