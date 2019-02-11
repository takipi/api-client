package com.takipi.api.client.util.cicd;

import java.util.List;

public class QualityGateReport {

	private List<OOReportEvent> newErrors;
	private List<OOReportEvent> resurfacedErrors;
	private List<OOReportEvent> criticalErrors;
	private List<OOReportEvent> topErrors;
	private long totalErrorCount = 0;
	private int uniqueErrorCount = 0;

	public int getUniqueErrorCount() {
		return uniqueErrorCount;
	}

	public void addToUniqueErrorCount(int value) {
		uniqueErrorCount += value;
	}

	public long getTotalErrorCount() {
		return totalErrorCount;
	}

	public void addToTotalErrorCount(long value) {
		totalErrorCount += value;
	}

	public List<OOReportEvent> getNewErrors() {
		return newErrors;
	}

	public List<OOReportEvent> getCriticalErrors() {
		return criticalErrors;
	}

	public void setNewErrors(List<OOReportEvent> newErrors) {
		this.newErrors = newErrors;
	}

	public void setCriticalErrors(List<OOReportEvent> criticalErrors) {
		this.criticalErrors = criticalErrors;
	}

	public List<OOReportEvent> getResurfacedErrors() {
		return resurfacedErrors;
	}

	public void setResurfacedErrors(List<OOReportEvent> resurfacedErrors) {
		this.resurfacedErrors = resurfacedErrors;
	}

	public List<OOReportEvent> getTopErrors() {
		return topErrors;
	}

	public void setTopErrors(List<OOReportEvent> topErrors) {
		this.topErrors = topErrors;
	}
}
