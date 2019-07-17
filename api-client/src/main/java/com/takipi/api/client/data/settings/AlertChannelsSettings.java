package com.takipi.api.client.data.settings;

public class AlertChannelsSettings
{
	public EmailAlertSettings email;
	public SlackAlertSettings slack;
	public HipChatAlertSettings hip_chat;
	public PagerDutyAlertSettings pager_duty;
	public WebhookAlertSettings webhook;
	public ServiceNowAlertSettings service_now;
	
	public static class EmailAlertSettings
	{
		public boolean alert_me;
		public boolean alert_all_team_members;
		public boolean alert_additional_emails;
		public String additional_emails_to_alert;
	}
	
	public static class SlackAlertSettings
	{
		public String inhook_url;
	}
	
	public static class HipChatAlertSettings
	{
		public String token;
		public String room;
		public String url;
	}
	
	public static class PagerDutyAlertSettings
	{
		public String service_integration_key;
	}
	
	public static class WebhookAlertSettings
	{
		public String webhook_url;
	}
	
	public static class ServiceNowAlertSettings
	{
		public String url;
		public String user_id;
		public String password;
		public String table;
	}
}
