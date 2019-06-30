package com.takipi.api.client.request.alertssettings;

import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.api.client.data.alertssetings.AlertsSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.EmailAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.HipChatAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.PagerDutyAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.ServiceNowAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.SlackAlertSettings;
import com.takipi.api.client.data.alertssetings.AlertsSettings.WebhookAlertSettings;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class UpdateDefaultAlertsSettingsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final AlertsSettings alertsSettings;
	private final InitializedFields initializedFields;

	protected UpdateDefaultAlertsSettingsRequest(String serviceId, AlertsSettings alertsSettings,
			InitializedFields initializedFields) {
		super(serviceId);

		this.alertsSettings = alertsSettings;
		this.initializedFields = initializedFields;
	}

	@Override
	public String postData() {
		Map<String, String> map = Maps.newHashMap();

		Map<String, String> emailsMap = Maps.newHashMap();

		if (this.initializedFields.emailAlertMeInitialized) {
			emailsMap.put("alert_me", Boolean.toString(alertsSettings.email.alert_me));
		}

		if (this.initializedFields.emailAlertAllTeamMembersInitialized) {
			emailsMap.put("alert_all_team_members", Boolean.toString(alertsSettings.email.alert_all_team_members));
		}

		if (this.initializedFields.emailAlertAdditionalEmailsInitialized) {
			emailsMap.put("alert_additional_emails", Boolean.toString(alertsSettings.email.alert_additional_emails));
		}

		if (alertsSettings.email.additional_emails_to_alert != null) {
			emailsMap.put("additional_emails_to_alert",
					JsonUtil.stringify(alertsSettings.email.additional_emails_to_alert));
		}

		if (!emailsMap.isEmpty()) {
			map.put("email", JsonUtil.createSimpleJson(emailsMap));
		}

		if (alertsSettings.slack != null) {
			Map<String, String> slackMap = Maps.newHashMap();
			slackMap.put("inhook_url", JsonUtil.stringify(alertsSettings.slack.inhook_url));
			map.put("slack", JsonUtil.createSimpleJson(slackMap));
		}

		if (alertsSettings.hip_chat != null) {
			Map<String, String> hipChatMap = Maps.newHashMap();

			hipChatMap.put("token", JsonUtil.stringify(alertsSettings.hip_chat.token));
			hipChatMap.put("room", JsonUtil.stringify(alertsSettings.hip_chat.room));

			if (alertsSettings.hip_chat.url != null) {
				hipChatMap.put("url", JsonUtil.stringify(alertsSettings.hip_chat.url));
			}
			map.put("hip_chat", JsonUtil.createSimpleJson(hipChatMap));
		}

		if (alertsSettings.pager_duty != null) {
			Map<String, String> pagerDutyMap = Maps.newHashMap();

			pagerDutyMap.put("service_integration_key",
					JsonUtil.stringify(alertsSettings.pager_duty.service_integration_key));

			map.put("pager_duty", JsonUtil.createSimpleJson(pagerDutyMap));
		}

		if (alertsSettings.webhook != null) {
			Map<String, String> webhookMap = Maps.newHashMap();

			webhookMap.put("webhook_url", JsonUtil.stringify(alertsSettings.webhook.webhook_url));

			map.put("webhook", JsonUtil.createSimpleJson(webhookMap));
		}

		if (alertsSettings.service_now != null) {
			Map<String, String> serviceNowMap = Maps.newHashMap();

			serviceNowMap.put("url", JsonUtil.stringify(alertsSettings.service_now.url));
			serviceNowMap.put("user_id", JsonUtil.stringify(alertsSettings.service_now.user_id));
			serviceNowMap.put("password", JsonUtil.stringify(alertsSettings.service_now.password));
			serviceNowMap.put("table", JsonUtil.stringify(alertsSettings.service_now.table));

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
		private AlertsSettings alertsSettings;
		private InitializedFields initializedFields;

		Builder() {
			this.alertsSettings = new AlertsSettings();
			this.alertsSettings.email = new EmailAlertSettings();
			this.initializedFields = new InitializedFields();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setEmailAlertMe(boolean emailAlertMe) {
			this.alertsSettings.email.alert_me = emailAlertMe;

			this.initializedFields.emailAlertMeInitialized = true;

			return this;
		}

		public Builder setEmailAlertAllTeamMembers(boolean emailAlertAllTeamMembers) {
			this.alertsSettings.email.alert_all_team_members = emailAlertAllTeamMembers;

			this.initializedFields.emailAlertAllTeamMembersInitialized = true;

			return this;
		}

		public Builder setEmailAlertAdditionalEmails(boolean emailAlertAdditionalEmails) {
			this.alertsSettings.email.alert_additional_emails = emailAlertAdditionalEmails;

			this.initializedFields.emailAlertAdditionalEmailsInitialized = true;

			return this;
		}

		public Builder setEmailAdditionalEmailsToAlerts(String emailAdditionalEmailsToAlerts) {
			this.alertsSettings.email.additional_emails_to_alert = emailAdditionalEmailsToAlerts;

			return this;
		}

		public Builder setSlackSettings(String slackInhookUrl) {
			this.alertsSettings.slack = new SlackAlertSettings();

			this.alertsSettings.slack.inhook_url = slackInhookUrl;

			return this;
		}

		public Builder setHipChatSettings(String hipChatToken, String hipChatRoom, String hipChatUrl) {
			this.alertsSettings.hip_chat = new HipChatAlertSettings();

			this.alertsSettings.hip_chat.token = hipChatToken;
			this.alertsSettings.hip_chat.room = hipChatRoom;
			this.alertsSettings.hip_chat.url = hipChatUrl;

			return this;
		}

		public Builder setPagerDutySettings(String pagerDutyServiceIntegrationKey) {
			this.alertsSettings.pager_duty = new PagerDutyAlertSettings();

			this.alertsSettings.pager_duty.service_integration_key = pagerDutyServiceIntegrationKey;

			return this;
		}

		public Builder setWebhookSettings(String webhookUrl) {
			this.alertsSettings.webhook = new WebhookAlertSettings();

			this.alertsSettings.webhook.webhook_url = webhookUrl;

			return this;
		}

		public Builder setServiceNowSettings(String serviceNowUrl, String serviceNowUserId, String serviceNowPassword,
				String serviceNowTable) {
			this.alertsSettings.service_now = new ServiceNowAlertSettings();

			this.alertsSettings.service_now.url = serviceNowUrl;
			this.alertsSettings.service_now.user_id = serviceNowUserId;
			this.alertsSettings.service_now.password = serviceNowPassword;
			this.alertsSettings.service_now.table = serviceNowTable;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if ((this.alertsSettings.slack != null) && (Strings.isNullOrEmpty(this.alertsSettings.slack.inhook_url))) {
				throw new IllegalArgumentException("No inhook URL supplied for Slack inhook connection request");
			}

			if (this.alertsSettings.hip_chat != null) {
				if ((Strings.isNullOrEmpty(this.alertsSettings.hip_chat.token))
						|| (Strings.isNullOrEmpty(this.alertsSettings.hip_chat.room))) {
					throw new IllegalArgumentException("No room or token supplied for HipChat connection request");
				}
			}

			if ((this.alertsSettings.pager_duty != null)
					&& (Strings.isNullOrEmpty(this.alertsSettings.pager_duty.service_integration_key))) {
				throw new IllegalArgumentException("No integration key supplied for PagerDuty connection request");
			}

			if ((this.alertsSettings.webhook != null)
					&& (Strings.isNullOrEmpty(this.alertsSettings.webhook.webhook_url))) {
				throw new IllegalArgumentException("No webhook URL supplied for webhook request");
			}

			if (this.alertsSettings.service_now != null) {
				if ((Strings.isNullOrEmpty(this.alertsSettings.service_now.url))
						|| (Strings.isNullOrEmpty(this.alertsSettings.service_now.user_id))
						|| (Strings.isNullOrEmpty(this.alertsSettings.service_now.password))
						|| (Strings.isNullOrEmpty(this.alertsSettings.service_now.table))) {
					throw new IllegalArgumentException("Not all settings supplied for ServiceNow integration");
				}
			}
		}

		public UpdateDefaultAlertsSettingsRequest build() {
			validate();

			return new UpdateDefaultAlertsSettingsRequest(serviceId, alertsSettings, initializedFields);
		}
	}

	public static class InitializedFields {
		public boolean emailAlertMeInitialized;
		public boolean emailAlertAllTeamMembersInitialized;
		public boolean emailAlertAdditionalEmailsInitialized;
	}
}
