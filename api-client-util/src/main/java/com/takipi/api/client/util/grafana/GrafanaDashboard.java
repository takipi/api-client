package com.takipi.api.client.util.grafana;

public enum GrafanaDashboard {
	HOME("lg0U4qriz", "home"),
	NEW_ERRORS("3NZ83NLik", "new-errors"),
	INCREASING_ERRORS("WZ739NYmk", "increasing-errors"),
	CRITICAL_ERRORS("y_EqZBDWk", "critical-errors"),
	SLOWDOWNS("iHSORcymk", "Slowdowns"),
	UNIQUE_ERRORS("HYBPMdYik", "errors");

	public final String dashboardId;
	public final String dashboardName;

	private GrafanaDashboard(String dashboardId, String dashboardName) {
		this.dashboardId = dashboardId;
		this.dashboardName = dashboardName;
	}
}
