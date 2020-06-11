package com.overops.report.service.model;

public class QualityReportLinkTemplate
{
	private final long urlCreationTimestamp;
	private final String link;

	public QualityReportLinkTemplate(long urlCreationTimestamp, String link) {
		this.urlCreationTimestamp = urlCreationTimestamp;
		this.link = link;
	}
	
	public long getUrlCreationTimestamp()
	{
		return urlCreationTimestamp;
	}
	
	public String getLink()
	{
		return link;
	}
}
