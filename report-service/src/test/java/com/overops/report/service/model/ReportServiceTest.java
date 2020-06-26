package com.overops.report.service.model;

import com.google.gson.Gson;
import com.overops.report.service.QualityReportParams;
import com.overops.report.service.ReportService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static com.overops.report.service.ReportService.DELAY_REPORT;
import static com.overops.report.service.ReportService.QUERY;
import static com.overops.report.service.ReportService.URL_CREATION_TIMESTAMP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportServiceTest
{
	@Test
	public void reportLink() throws URISyntaxException
	{
		QualityReportParams params = new QualityReportParams();
		params.setServiceId("S1");
		params.setMarkUnstable(true);
		params.setApplicationName("NewApp");
		ReportService reportService = new ReportService();

		String reportLink = reportService.generateReportLink("http://localhost", params, null, false);

		URIBuilder uriBuilder = new URIBuilder(reportLink);
		List<NameValuePair> queryParams = uriBuilder.getQueryParams();

		String jsonParams = new Gson().toJson(params);
		assertEquals(jsonParams, retrieveParam(queryParams, QUERY).getValue());
		NameValuePair urlCreationTimestamp = retrieveParam(queryParams, URL_CREATION_TIMESTAMP);
		long now = DateTime.now().getMillis() / 1000L;
		long timestampDifference = now - Long.parseLong(urlCreationTimestamp.getValue());

		// Just making sure it is with a range since we are losing precision
		assertTrue(timestampDifference < 2);
	}

	@Test
	public void isReportReady()
	{
		ReportService reportService = new ReportService();
		assertFalse(reportService.isQualityReportReady(String.valueOf(DateTime.now().getMillis() / 1000L)));
		assertTrue(reportService.isQualityReportReady(String.valueOf((DateTime.now().getMillis() / 1000L) - DELAY_REPORT)));
	}

	@Test
	public void timeLeftTillReportIsReady()
	{
		ReportService reportService = new ReportService();
		long delaySeconds = reportService.secondsLeftForQualityReport(String.valueOf(DateTime.now().getMillis() / 1000L));
		assertTrue(delaySeconds > 88 && delaySeconds <= 90);

		// Check no delay
		delaySeconds = reportService.secondsLeftForQualityReport(String.valueOf((DateTime.now().getMillis() / 1000L) - DELAY_REPORT));
		assertEquals(0, delaySeconds);

		// Check even if the time is well past that the delay is 0 seconds
		delaySeconds = reportService.secondsLeftForQualityReport(String.valueOf((DateTime.now().getMillis() / 1000L) - DELAY_REPORT - DELAY_REPORT));
		assertEquals(0, delaySeconds);
	}

	private NameValuePair retrieveParam(List<NameValuePair> queryParams, String name) {
		return queryParams.stream().filter(pair -> pair.getName().equals(name)).findFirst().get();
	}
}
