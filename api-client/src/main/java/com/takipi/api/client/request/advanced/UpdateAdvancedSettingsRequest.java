package com.takipi.api.client.request.advanced;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.takipi.api.client.data.advanced.AdvancedSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class UpdateAdvancedSettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final AdvancedSettings advancedSettings;
	private final boolean isShowLogLinksInitialized;
	
	protected UpdateAdvancedSettingsRequest(String serviceId, AdvancedSettings advancedSettings, boolean isShowLogLinksInitialized)
	{
		super(serviceId);
		
		this.advancedSettings = advancedSettings;
		this.isShowLogLinksInitialized = isShowLogLinksInitialized;
	}
	
	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
		
		if (advancedSettings.allowed_ips != null)
		{
			map.put("allowed_ips", JsonUtil.stringify(advancedSettings.allowed_ips));
		}
		
		map.put("show_rethrows", Boolean.toString(advancedSettings.show_rethrows));
		
		if (isShowLogLinksInitialized)
		{
			map.put("show_log_links", Boolean.toString(advancedSettings.show_log_links));
		}
		
		map.put("clear_env_filters", Boolean.toString(advancedSettings.clear_env_filters));
		
		return JsonUtil.createSimpleJson(map, false).getBytes(ApiConstants.UTF8_ENCODING);
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
		private String allowedIps;
		private boolean showRethrows;
		private boolean isShowLogLinksInitialized;
		private boolean showLogLinks;
		private boolean clearEnvFilters;
		
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder setAllowedIps(String allowedIps) {
			this.allowedIps = allowedIps;
			
			return this;
		}
		
		public Builder setShowRethrows(boolean showRethrows) {
			this.showRethrows = showRethrows;
			
			return this;
		}
		
		public Builder setShowLogLinks(boolean showLogLinks) {
			this.showLogLinks = showLogLinks;
			this.isShowLogLinksInitialized = true;
			
			return this;
		}
		
		public Builder setClearEnvFilters(boolean clearEnvFilters) {
			this.clearEnvFilters = clearEnvFilters;
			
			return this;
		}
		
		public UpdateAdvancedSettingsRequest build() {
			validate();
			
			AdvancedSettings advancedSettings = new AdvancedSettings();

			advancedSettings.allowed_ips = allowedIps;
			advancedSettings.show_rethrows = showRethrows;
			advancedSettings.show_log_links = showLogLinks;
			advancedSettings.clear_env_filters = clearEnvFilters;

			return new UpdateAdvancedSettingsRequest(serviceId, advancedSettings, isShowLogLinksInitialized);
		}
	}
}
