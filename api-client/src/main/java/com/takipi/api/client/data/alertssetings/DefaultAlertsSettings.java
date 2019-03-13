package com.takipi.api.client.data.alertssetings;

public class DefaultAlertsSettings
{
	public AlertsDefaultEmailSettings email;
	public AlertsDefaultSlackSettings slack;
	public AlertsDefaultHipChatSettings hip_chat;
	public AlertsDefaultPagerDutySettings pager_duty;
	public AlertsDefaultWebhookSettings webhook;
	public AlertsDefaultServiceNowSettings service_now;
	
	public static class AlertsDefaultEmailSettings
	{
		public boolean alert_me;
		public boolean alert_all_team_members;
		public boolean alert_additional_emails;
		public String additional_emails_to_alert;
	}
	
	public static class AlertsDefaultSlackSettings
	{
		public String inhook_url;
	}
	
	public static class AlertsDefaultHipChatSettings
	{
		public String token;
		public String room;
		public String url;
	}
	
	public static class AlertsDefaultPagerDutySettings
	{
		public String service_integration_key;
	}
	
	public static class AlertsDefaultWebhookSettings
	{
		public String webhook_url;
	}
	
	public static class AlertsDefaultServiceNowSettings
	{
		public String url;
		public String user_id;
		public String password;
		public String table;
	}
}