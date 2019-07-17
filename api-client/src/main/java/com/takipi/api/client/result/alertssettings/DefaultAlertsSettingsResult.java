package com.takipi.api.client.result.alertssettings;

import com.takipi.api.client.data.settings.AlertChannelsSettings.EmailAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.HipChatAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.PagerDutyAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.ServiceNowAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.SlackAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.WebhookAlertSettings;
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
