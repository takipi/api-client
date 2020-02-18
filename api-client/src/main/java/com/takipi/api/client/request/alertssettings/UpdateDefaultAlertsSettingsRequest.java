package com.takipi.api.client.request.alertssettings;

import java.util.HashMap;
import java.util.Map;

import com.takipi.api.client.data.settings.AlertChannelsSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.EmailAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.HipChatAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.PagerDutyAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.ServiceNowAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.SlackAlertSettings;
import com.takipi.api.client.data.settings.AlertChannelsSettings.WebhookAlertSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class UpdateDefaultAlertsSettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final AlertChannelsSettings alertChannelsSettings;
	private final InitializedFields initializedFields;

	protected UpdateDefaultAlertsSettingsRequest(String serviceId, AlertChannelsSettings alertChannelsSettings,
			InitializedFields initializedFields) {
		super(serviceId);

		this.alertChannelsSettings = alertChannelsSettings;
		this.initializedFields = initializedFields;
	}

	@Override
	public String postData() {
		Map<String, String> map = new HashMap<>();

		Map<String, String> emailsMap = new HashMap<>();

		if (this.initializedFields.emailAlertMeInitialized) {
			emailsMap.put("alert_me", Boolean.toString(alertChannelsSettings.email.alert_me));
		}

		if (this.initializedFields.emailAlertAllTeamMembersInitialized) {
			emailsMap.put("alert_all_team_members",
					Boolean.toString(alertChannelsSettings.email.alert_all_team_members));
		}

		if (this.initializedFields.emailAlertAdditionalEmailsInitialized) {
			emailsMap.put("alert_additional_emails",
					Boolean.toString(alertChannelsSettings.email.alert_additional_emails));
		}

		if (alertChannelsSettings.email.additional_emails_to_alert != null) {
			emailsMap.put("additional_emails_to_alert",
					JsonUtil.stringify(alertChannelsSettings.email.additional_emails_to_alert));
		}

		if (!emailsMap.isEmpty()) {
			map.put("email", JsonUtil.createSimpleJson(emailsMap));
		}

		if (alertChannelsSettings.slack != null) {
			Map<String, String> slackMap = new HashMap<>();
			slackMap.put("inhook_url", JsonUtil.stringify(alertChannelsSettings.slack.inhook_url));
			map.put("slack", JsonUtil.createSimpleJson(slackMap));
		}

		if (alertChannelsSettings.hip_chat != null) {
			Map<String, String> hipChatMap = new HashMap<>();

			hipChatMap.put("token", JsonUtil.stringify(alertChannelsSettings.hip_chat.token));
			hipChatMap.put("room", JsonUtil.stringify(alertChannelsSettings.hip_chat.room));

			if (alertChannelsSettings.hip_chat.url != null) {
				hipChatMap.put("url", JsonUtil.stringify(alertChannelsSettings.hip_chat.url));
			}
			map.put("hip_chat", JsonUtil.createSimpleJson(hipChatMap));
		}

		if (alertChannelsSettings.pager_duty != null) {
			Map<String, String> pagerDutyMap = new HashMap<>();

			pagerDutyMap.put("service_integration_key",
					JsonUtil.stringify(alertChannelsSettings.pager_duty.service_integration_key));

			map.put("pager_duty", JsonUtil.createSimpleJson(pagerDutyMap));
		}

		if (alertChannelsSettings.webhook != null) {
			Map<String, String> webhookMap = new HashMap<>();

			webhookMap.put("webhook_url", JsonUtil.stringify(alertChannelsSettings.webhook.webhook_url));

			map.put("webhook", JsonUtil.createSimpleJson(webhookMap));
		}

		if (alertChannelsSettings.service_now != null) {
			Map<String, String> serviceNowMap = new HashMap<>();

			serviceNowMap.put("url", JsonUtil.stringify(alertChannelsSettings.service_now.url));
			serviceNowMap.put("user_id", JsonUtil.stringify(alertChannelsSettings.service_now.user_id));
			serviceNowMap.put("password", JsonUtil.stringify(alertChannelsSettings.service_now.password));
			serviceNowMap.put("table", JsonUtil.stringify(alertChannelsSettings.service_now.table));

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
		private AlertChannelsSettings alertChannelsSettings;
		private InitializedFields initializedFields;

		Builder() {
			this.alertChannelsSettings = new AlertChannelsSettings();
			this.alertChannelsSettings.email = new EmailAlertSettings();
			this.initializedFields = new InitializedFields();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setEmailAlertMe(boolean emailAlertMe) {
			this.alertChannelsSettings.email.alert_me = emailAlertMe;

			this.initializedFields.emailAlertMeInitialized = true;

			return this;
		}

		public Builder setEmailAlertAllTeamMembers(boolean emailAlertAllTeamMembers) {
			this.alertChannelsSettings.email.alert_all_team_members = emailAlertAllTeamMembers;

			this.initializedFields.emailAlertAllTeamMembersInitialized = true;

			return this;
		}

		public Builder setEmailAlertAdditionalEmails(boolean emailAlertAdditionalEmails) {
			this.alertChannelsSettings.email.alert_additional_emails = emailAlertAdditionalEmails;

			this.initializedFields.emailAlertAdditionalEmailsInitialized = true;

			return this;
		}

		public Builder setEmailAdditionalEmailsToAlerts(String emailAdditionalEmailsToAlerts) {
			this.alertChannelsSettings.email.additional_emails_to_alert = emailAdditionalEmailsToAlerts;

			return this;
		}

		public Builder setSlackSettings(String slackInhookUrl) {
			this.alertChannelsSettings.slack = new SlackAlertSettings();

			this.alertChannelsSettings.slack.inhook_url = slackInhookUrl;

			return this;
		}

		public Builder setHipChatSettings(String hipChatToken, String hipChatRoom, String hipChatUrl) {
			this.alertChannelsSettings.hip_chat = new HipChatAlertSettings();

			this.alertChannelsSettings.hip_chat.token = hipChatToken;
			this.alertChannelsSettings.hip_chat.room = hipChatRoom;
			this.alertChannelsSettings.hip_chat.url = hipChatUrl;

			return this;
		}

		public Builder setPagerDutySettings(String pagerDutyServiceIntegrationKey) {
			this.alertChannelsSettings.pager_duty = new PagerDutyAlertSettings();

			this.alertChannelsSettings.pager_duty.service_integration_key = pagerDutyServiceIntegrationKey;

			return this;
		}

		public Builder setWebhookSettings(String webhookUrl) {
			this.alertChannelsSettings.webhook = new WebhookAlertSettings();

			this.alertChannelsSettings.webhook.webhook_url = webhookUrl;

			return this;
		}

		public Builder setServiceNowSettings(String serviceNowUrl, String serviceNowUserId, String serviceNowPassword,
				String serviceNowTable) {
			this.alertChannelsSettings.service_now = new ServiceNowAlertSettings();

			this.alertChannelsSettings.service_now.url = serviceNowUrl;
			this.alertChannelsSettings.service_now.user_id = serviceNowUserId;
			this.alertChannelsSettings.service_now.password = serviceNowPassword;
			this.alertChannelsSettings.service_now.table = serviceNowTable;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			SlackAlertSettings slackSettings = alertChannelsSettings.slack;

			if ((slackSettings != null) && (StringUtil.isNullOrEmpty(slackSettings.inhook_url))) {
				throw new IllegalArgumentException("No inhook URL supplied for Slack inhook connection request");
			}

			HipChatAlertSettings hipChatSettings = alertChannelsSettings.hip_chat;

			if (hipChatSettings != null) {
				if ((StringUtil.isNullOrEmpty(hipChatSettings.token)) || (StringUtil.isNullOrEmpty(hipChatSettings.room))) {
					throw new IllegalArgumentException("No room or token supplied for HipChat connection request");
				}
			}

			PagerDutyAlertSettings pagerDutySettings = alertChannelsSettings.pager_duty;

			if ((pagerDutySettings != null) && (StringUtil.isNullOrEmpty(pagerDutySettings.service_integration_key))) {
				throw new IllegalArgumentException("No integration key supplied for PagerDuty connection request");
			}

			WebhookAlertSettings webhookSettings = alertChannelsSettings.webhook;

			if ((webhookSettings != null) && (StringUtil.isNullOrEmpty(webhookSettings.webhook_url))) {
				throw new IllegalArgumentException("No webhook URL supplied for webhook request");
			}

			ServiceNowAlertSettings serviceNowSettings = alertChannelsSettings.service_now;

			if (serviceNowSettings != null) {
				if ((StringUtil.isNullOrEmpty(serviceNowSettings.url))
						|| (StringUtil.isNullOrEmpty(serviceNowSettings.user_id))
						|| (StringUtil.isNullOrEmpty(serviceNowSettings.password))
						|| (StringUtil.isNullOrEmpty(serviceNowSettings.table))) {
					throw new IllegalArgumentException("Not all settings supplied for ServiceNow integration");
				}
			}
		}

		public UpdateDefaultAlertsSettingsRequest build() {
			validate();

			return new UpdateDefaultAlertsSettingsRequest(serviceId, alertChannelsSettings, initializedFields);
		}
	}

	public static class InitializedFields {
		public boolean emailAlertMeInitialized;
		public boolean emailAlertAllTeamMembersInitialized;
		public boolean emailAlertAdditionalEmailsInitialized;
	}
}
