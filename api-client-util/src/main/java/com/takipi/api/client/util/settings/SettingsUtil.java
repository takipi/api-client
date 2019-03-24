package com.takipi.api.client.util.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.request.reliability.GetReliabilitySettingsRequest;
import com.takipi.api.client.result.reliability.GetReliabilitySettingsResult;
import com.takipi.api.core.url.UrlClient.Response;

public class SettingsUtil {
	private static final Logger logger = LoggerFactory.getLogger(SettingsUtil.class);

	public static ServiceSettingsData getServiceReliabilitySettings(ApiClient apiClient, String serviceId) {
		GetReliabilitySettingsRequest request = GetReliabilitySettingsRequest.newBuilder().setServiceId(serviceId)
				.build();

		Response<GetReliabilitySettingsResult> response = apiClient.get(request);

		if ((response.isBadResponse()) || (response.data == null)
				|| (Strings.isNullOrEmpty(response.data.reliability_settings_json))) {
			logger.warn("Failed getting reliability settings for {}.", serviceId);
			return null;
		}

		try {
			return new Gson().fromJson(response.data.reliability_settings_json, ServiceSettingsData.class);
		} catch (Exception e) {
			logger.warn("Failed parsing service settings for {}.", serviceId);
			return null;
		}
	}
}
