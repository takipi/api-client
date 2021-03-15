package com.takipi.api.client.util.cicd;

import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.regression.RegressionStringUtil;

import java.util.List;

public class OOReportEvent {

	private EventResult event;
	private String arcLink;
	private String type;
	private String applications;
	private List<String> labels;

	public OOReportEvent(EventResult event, String arcLink) {
		this(event, null, arcLink);
	}

	public OOReportEvent(EventResult event, String type, String arcLink) {
		this.event = event;
		this.arcLink = arcLink;
		this.type = type;
		this.labels = event.labels;
	}

	public EventResult getEvent() {
		return event;
	}

	public String getEventSummary() {
		String result = RegressionStringUtil.getEventSummary(event);
		return result;
	}

	public String getEventRate() {
		String result = RegressionStringUtil.getEventRate(event);
		return result;
	}

	public String getIntroducedBy() {
		String result = RegressionStringUtil.getIntroducedBy(event);
		return result;
	}

	public String getType() {
		return type;
	}

	public String getARCLink() {
		return arcLink;
	}

	public void setArcLink(String arcLink) {
		this.arcLink = arcLink;
	}

	public long getHits() {
		return event.stats.hits;
	}
	
	public void setApplications(String app) {
		if (applications == null) {
			applications = app;
		} else {
			applications = applications + ", " + app;
		}
	}
	
	public String getApplications() {
		return applications;
	}

	public long getCalls() {
		return event.stats.invocations;
	}

	public List<String> getLabels() {
		return this.labels;
	}

	@Override
	public String toString() {
		String result = getEventSummary() + " " + getEventRate();
		return result;
	}
}
