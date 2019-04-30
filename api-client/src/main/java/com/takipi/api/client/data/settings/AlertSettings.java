package com.takipi.api.client.data.settings;

import java.util.List;

public class AlertSettings {
	public String view_id;
	public String view;
	public List<String> categories;
	public Alerts alerts;

	public static class Alerts {
		public boolean global;
		public boolean alert_on_new;
		public boolean alert_on_every_entrypoint;
		public AnomelyAlert anomaly_alert;
		public List<String> channels;
		public AlertFunction channel_function;
	}

	public static class AnomelyAlert {
		public boolean alert_on_anomaly;
		public AlertFunction anomaly_function;
		public int period;
	}

	public static class AlertFunction {
		public String library_id;
		public String function_id;
		public String summary;
		public String args;
	}
}
