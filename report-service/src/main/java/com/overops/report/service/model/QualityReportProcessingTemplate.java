package com.overops.report.service.model;

public class QualityReportProcessingTemplate
{
	private final long countDown;

	public QualityReportProcessingTemplate(long countDown) {
		this.countDown = countDown;
	}

	public long getCountDown() {
		return countDown;
	}
}