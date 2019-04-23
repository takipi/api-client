package com.takipi.api.client.result.settings;

import java.util.List;

import com.takipi.api.client.data.settings.AlertSettings;
import com.takipi.api.core.result.intf.ApiResult;

public class AlertsSettingsResult implements ApiResult {
	public List<AlertSettings> alerts;
}
