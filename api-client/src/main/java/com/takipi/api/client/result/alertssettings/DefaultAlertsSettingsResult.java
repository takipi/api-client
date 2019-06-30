package com.takipi.api.client.result.alertssettings;

import com.takipi.api.client.data.alertssetings.AlertsSettings.EmailAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.HipChatAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.PagerDutyAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.ServiceNowAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.SlackAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.WebhookAlertSettings;
import com.takipi.api.core.result.intf.ApiResult;

public class DefaultAlertsSettingsResult implements ApiResult
{
	public EmailAlertSettings email;
	public SlackAlertSettings slack;
	public HipChatAlertSettings hip_chat;
	public PagerDutyAlertSettings pager_duty;
	public WebhookAlertSettings webhook;
	public ServiceNowAlertSettings service_now;
}
