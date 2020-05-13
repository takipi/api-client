package com.overops.report.service.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.assertj.core.internal.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.overops.report.service.model.QualityGateTestResults.TestType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.overops.report.service.model.QualityGateTestResults.TestType;

public class QualityReportTest {

    private static final String SUMMARY_TEMPLATE = "Summary: %s  (%s)";

    @Test
    public void test1() throws Exception {
        String testResult = "testQualityReport1.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = new QualityReport();
        qualityReport.setStatusMsg(String.format(SUMMARY_TEMPLATE, "Passed No Events", testResult));
        qualityReport.setStatusCode(QualityReport.ReportStatus.PASSED);

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
        qualityReport.setStatusCode(QualityReport.ReportStatus.WARNING);

        qualityReport.getNewErrorsTestResults().setPassed(true);
        qualityReport.getResurfacedErrorsTestResults().setPassed(true);
        qualityReport.getCriticalErrorsTestResults().setPassed(true);
        qualityReport.getRegressionErrorsTestResults().setPassed(true);
        qualityReport.getTotalErrorsTestResults().setPassed(true);
        qualityReport.getUniqueErrorsTestResults().setPassed(true);

        String actual = qualityReport.toHtml(false);
        assertEqualsWithoutWhiteSpace(expected, actual);
    }

    @Test
    public void test4() throws Exception {
        String testResult = "testQualityReport4.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = generateBaseModel();
        qualityReport.setStatusMsg(String.format(SUMMARY_TEMPLATE, "Exception Case", testResult));

        QualityReportExceptionDetails exceptionDetails = new QualityReportExceptionDetails();
        exceptionDetails.setEmailMessage("myEmailMessage");
        try{
            throw new IllegalArgumentException("Some error thrown.");
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String[] stackTrace = sw.toString().split("\n");
            exceptionDetails.setExceptionMessage(e.getMessage());
            exceptionDetails.setStackTrace(stackTrace);
        }
        qualityReport.setExceptionDetails(exceptionDetails);

        String actual = qualityReport.toHtml(false);
        assertEqualsWithoutWhiteSpace(expected, actual);
    }

    private QualityReport generateBaseModel() throws Exception {
        String json = resourceToString("baseModel.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, QualityReport.class);
    }

    private void assertEqualsWithoutWhiteSpace(String expected, String actual) {
        assertEquals(expected.replaceAll(" ", ""), actual.replaceAll(" ", ""));
    }

    protected String resourceToString(String resourceName) throws IOException {
        return IOUtils.toString(QualityReportTest.class.getResource(resourceName), Charset.defaultCharset());
    }
}
