package com.overops.report.service.model;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

public class ReportServiceTest
{
	@Test
	public void reportLink() throws URISyntaxException
	{
		// TODO ccaspanello Rewrite test
//		QualityReportParams params = new QualityReportParams();
//		params.setMarkUnstable(true);
//		params.setApplicationName("NewApp");
//		ReportService reportService = new ReportService();
//
//		String reportLink = reportService.generateReportLink("http://localhost", params);
//
//		URIBuilder uriBuilder = new URIBuilder(reportLink);
//		List<NameValuePair> queryParams = uriBuilder.getQueryParams();
//
//		String jsonParams = new Gson().toJson(params);
//		assertEquals(jsonParams, retrieveParam(queryParams, QUERY).getValue());
//		NameValuePair urlCreationTimestamp = retrieveParam(queryParams, URL_CREATION_TIMESTAMP);
//		long now = DateTime.now().getMillis() / 1000L;
//		long timestampDifference = now - Long.parseLong(urlCreationTimestamp.getValue());
//
//		// Just making sure it is with a range since we are losing precision
//		assertTrue(timestampDifference < 2);
	}

}
