package com.takipi.api.client.result.alertssettings;

import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultEmailSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultHipChatSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultPagerDutySettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultServiceNowSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultSlackSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultWebhookSettings;
import com.takipi.api.core.result.intf.ApiResult;

public class DefaultAlertsSettingsResult implements ApiResult
{
	public AlertsDefaultEmailSettings email;
	public AlertsDefaultSlackSettings slack;
	public AlertsDefaultHipChatSettings hip_chat;
	public AlertsDefaultPagerDutySettings pager_duty;
	public AlertsDefaultWebhookSettings webhook;
	public AlertsDefaultServiceNowSettings service_now;
}
