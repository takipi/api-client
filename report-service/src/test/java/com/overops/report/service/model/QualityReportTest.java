package com.overops.report.service.model;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static com.overops.report.service.model.QualityReport.ReportStatus;

public class QualityReportTest extends AbstractQualityReportTest {

    public static final String SUMMARY_TEMPLATE = "Summary: %s  (%s)";

    @Test
    public void test1() throws Exception {
        String testResult = "testQualityReport1.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = new QualityReport();
        qualityReport.setStatusMsg(String.format(SUMMARY_TEMPLATE, "Passed No Events", testResult));
        qualityReport.setStatusCode(ReportStatus.PASSED);

        String actual = qualityReport.toHtml();
        assertEqualsWithoutWhiteSpace(expected, actual);
    }

    @Test
    public void test2() throws Exception {
        String testResult = "testQualityReport2.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = generateBaseModel();
        qualityReport.setStatusMsg(String.format(SUMMARY_TEMPLATE, "Failed All Events", testResult));

        String actual = qualityReport.toHtml(true);
        assertEqualsWithoutWhiteSpace(expected, actual);
    }

    @Test
    public void test3() throws Exception {
        String testResult = "testQualityReport3.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = generateBaseModel();
        qualityReport.setStatusMsg(String.format(SUMMARY_TEMPLATE, "Pass All Events", testResult));
        qualityReport.setStatusCode(ReportStatus.WARNING);

        qualityReport.getNewErrorsTestResults().setPassed(true);
        qualityReport.getResurfacedErrorsTestResults().setPassed(true);
        qualityReport.getCriticalErrorsTestResults().setPassed(true);
        qualityReport.getRegressionErrorsTestResults().setPassed(true);
        qualityReport.getTotalErrorsTestResults().setPassed(true);
        qualityReport.getUniqueErrorsTestResults().setPassed(true);

        String actual = qualityReport.toHtml(false);
        assertEqualsWithoutWhiteSpace(expected, actual);
    }
}
