package com.takipi.api.client.request.alertssettings;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultEmailSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultHipChatSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultPagerDutySettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultSlackSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultServiceNowSettings;
import com.takipi.api.client.data.alertssetings.DefaultAlertsSettings.AlertsDefaultWebhookSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

import java.util.Map;

public class UpdateAlertsSettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult>
{
	private final DefaultAlertsSettings defaultAlertsSettings;
	private final InitializedFields initializedFields;
	
	protected UpdateAlertsSettingsRequest(String serviceId, DefaultAlertsSettings defaultAlertsSettings, InitializedFields initializedFields) {
		super(serviceId);
		
		this.defaultAlertsSettings = defaultAlertsSettings;
		this.initializedFields = initializedFields;
	}
	
	@Override
	public String postData() {
		Map<String, String> map = Maps.newHashMap();
		
		Map<String, String> emailsMap = Maps.newHashMap();
		
		if (this.initializedFields.emailAlertMeInitialized) {
			emailsMap.put("alert_me", Boolean.toString(defaultAlertsSettings.email.alert_me));
		}
		
		if (this.initializedFields.emailAlertAllTeamMembersInitialized) {
			emailsMap.put("alert_all_team_members", Boolean.toString(defaultAlertsSettings.email.alert_all_team_members));
		}
		
		if (this.initializedFields.emailAlertAdditionalEmailsInitialized) {
			emailsMap.put("alert_additional_emails", Boolean.toString(defaultAlertsSettings.email.alert_additional_emails));
		}
		
		if (defaultAlertsSettings.email.additional_emails_to_alert != null) {
			emailsMap.put("additional_emails_to_alert", JsonUtil.stringify(defaultAlertsSettings.email.additional_emails_to_alert));
		}
		
		if (!emailsMap.isEmpty())
		{
			map.put("email", JsonUtil.createSimpleJson(emailsMap));
		}
		
		if (defaultAlertsSettings.slack != null) {
			Map<String, String> slackMap = Maps.newHashMap();
			slackMap.put("inhook_url", JsonUtil.stringify(defaultAlertsSettings.slack.inhook_url));
			map.put("slack", JsonUtil.createSimpleJson(slackMap));
		}
		
		if (defaultAlertsSettings.hip_chat != null) {
			Map<String, String> hipChatMap = Maps.newHashMap();
			
			hipChatMap.put("token", JsonUtil.stringify(defaultAlertsSettings.hip_chat.token));
			hipChatMap.put("room", JsonUtil.stringify(defaultAlertsSettings.hip_chat.room));
			
			if (defaultAlertsSettings.hip_chat.url != null) {
				hipChatMap.put("url", JsonUtil.stringify(defaultAlertsSettings.hip_chat.url));
			}
			map.put("hip_chat", JsonUtil.createSimpleJson(hipChatMap));
		}
		
		if (defaultAlertsSettings.pager_duty != null) {
			Map<String, String> pagerDutyMap = Maps.newHashMap();
			
			pagerDutyMap.put("service_integration_key", JsonUtil.stringify(defaultAlertsSettings.pager_duty.service_integration_key));
			
			map.put("pager_duty", JsonUtil.createSimpleJson(pagerDutyMap));
		}
		
		
		if (defaultAlertsSettings.webhook != null) {
			Map<String, String> webhookMap = Maps.newHashMap();
			
			webhookMap.put("webhook_url", JsonUtil.stringify(defaultAlertsSettings.webhook.webhook_url));
			
			map.put("webhook", JsonUtil.createSimpleJson(webhookMap));
		}
		
		
		if (defaultAlertsSettings.service_now != null) {
			Map<String, String> serviceNowMap = Maps.newHashMap();
			
			serviceNowMap.put("url", JsonUtil.stringify(defaultAlertsSettings.service_now.url));
			serviceNowMap.put("user_id", JsonUtil.stringify(defaultAlertsSettings.service_now.user_id));
			serviceNowMap.put("password", JsonUtil.stringify(defaultAlertsSettings.service_now.password));
			serviceNowMap.put("table", JsonUtil.stringify(defaultAlertsSettings.service_now.table));
			
			map.put("service_now", JsonUtil.createSimpleJson(serviceNowMap));
		}
		
		return JsonUtil.createSimpleJson(map, false);
	}
	
	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/settings/alerts";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private DefaultAlertsSettings defaultAlertsSettings;
		private InitializedFields initializedFields;
		
		Builder() {
			this.defaultAlertsSettings = new DefaultAlertsSettings();
			this.defaultAlertsSettings.email = new AlertsDefaultEmailSettings();
			this.initializedFields = new InitializedFields();
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder setEmailAlertMe(boolean emailAlertMe) {
			this.defaultAlertsSettings.email.alert_me = emailAlertMe;
			
			this.initializedFields.emailAlertMeInitialized = true;
			
			return this;
		}
		
		public Builder setEmailAlertAllTeamMembers(boolean emailAlertAllTeamMembers) {
			this.defaultAlertsSettings.email.alert_all_team_members = emailAlertAllTeamMembers;
			
			this.initializedFields.emailAlertAllTeamMembersInitialized = true;
			
			return this;
		}
		
		public Builder setEmailAlertAdditionalEmails(boolean emailAlertAdditionalEmails) {
			this.defaultAlertsSettings.email.alert_additional_emails = emailAlertAdditionalEmails;
			
			this.initializedFields.emailAlertAdditionalEmailsInitialized = true;
			
			return this;
		}
		
		public Builder setEmailAdditionalEmailsToAlerts(String emailAdditionalEmailsToAlerts) {
			this.defaultAlertsSettings.email.additional_emails_to_alert = emailAdditionalEmailsToAlerts;
			
			return this;
		}
		
		public Builder setSlackSettings(String slackInhookUrl) {
			this.defaultAlertsSettings.slack = new AlertsDefaultSlackSettings();
			
			this.defaultAlertsSettings.slack.inhook_url = slackInhookUrl;
			
			return this;
		}
		
		public Builder setHipChatSettings(String hipChatToken, String hipChatRoom, String hipChatUrl) {
			this.defaultAlertsSettings.hip_chat = new AlertsDefaultHipChatSettings();
			
			this.defaultAlertsSettings.hip_chat.token = hipChatToken;
			this.defaultAlertsSettings.hip_chat.room = hipChatRoom;
			this.defaultAlertsSettings.hip_chat.url = hipChatUrl;
			
			
			return this;
		}
		
		public Builder setPagerDutySettings(String pagerDutyServiceIntegrationKey) {
			this.defaultAlertsSettings.pager_duty = new AlertsDefaultPagerDutySettings();
			
			this.defaultAlertsSettings.pager_duty.service_integration_key = pagerDutyServiceIntegrationKey;
			
			return this;
		}
		
		public Builder setWebhookSettings(String webhookUrl) {
			this.defaultAlertsSettings.webhook = new AlertsDefaultWebhookSettings();
			
			this.defaultAlertsSettings.webhook.webhook_url = webhookUrl;
			
			return this;
		}
		
		public Builder setServiceNowSettings(String serviceNowUrl, String serviceNowUserId, String serviceNowPassword,
				String serviceNowTable) {
			this.defaultAlertsSettings.service_now = new AlertsDefaultServiceNowSettings();
			
			this.defaultAlertsSettings.service_now.url = serviceNowUrl;
			this.defaultAlertsSettings.service_now.user_id = serviceNowUserId;
			this.defaultAlertsSettings.service_now.password = serviceNowPassword;
			this.defaultAlertsSettings.service_now.table = serviceNowTable;

			return this;
		}
		
		@Override
		protected void validate() {
			super.validate();
			
			if ((this.defaultAlertsSettings.slack != null) &&
				(Strings.isNullOrEmpty(this.defaultAlertsSettings.slack.inhook_url)))
			{
				throw new IllegalArgumentException("No inhook URL supplied for Slack inhook connection request");
			}
			
			if (this.defaultAlertsSettings.hip_chat != null)
			{
				if ((Strings.isNullOrEmpty(this.defaultAlertsSettings.hip_chat.token)) ||
				 	(Strings.isNullOrEmpty(this.defaultAlertsSettings.hip_chat.room)))
				{
					throw new IllegalArgumentException("No room or token supplied for HipChat connection request");
				}
			}
			
			if ((this.defaultAlertsSettings.pager_duty != null) &&
				(Strings.isNullOrEmpty(this.defaultAlertsSettings.pager_duty.service_integration_key)))
			{
				throw new IllegalArgumentException("No integration key supplied for PagerDuty connection request");
			}
			
			if ((this.defaultAlertsSettings.webhook != null) &&
				(Strings.isNullOrEmpty(this.defaultAlertsSettings.webhook.webhook_url)))
			{
				throw new IllegalArgumentException("No webhook URL supplied for webhook request");
			}
			
			if (this.defaultAlertsSettings.service_now != null)
			{
				if ((Strings.isNullOrEmpty(this.defaultAlertsSettings.service_now.url)) ||
					(Strings.isNullOrEmpty(this.defaultAlertsSettings.service_now.user_id)) ||
					(Strings.isNullOrEmpty(this.defaultAlertsSettings.service_now.password)) ||
					(Strings.isNullOrEmpty(this.defaultAlertsSettings.service_now.table)))
				{
					throw new IllegalArgumentException("Not all settings supplied for ServiceNow integration");
				}
			}
		}
		
		public UpdateAlertsSettingsRequest build() {
			validate();
			
			return new UpdateAlertsSettingsRequest(serviceId, defaultAlertsSettings, initializedFields);
		}
	}
	
	public static class InitializedFields
	{
		public boolean emailAlertMeInitialized;
		public boolean emailAlertAllTeamMembersInitialized;
		public boolean emailAlertAdditionalEmailsInitialized;
	}
}
