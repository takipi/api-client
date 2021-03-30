package com.overops.report.service.model;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QualityReportExceptionTest extends AbstractQualityReportTest {

    @Test
    public void testException() throws Exception {
        String testResult = "testException.html";
        String expected = resourceToString(testResult);

        QualityReport qualityReport = generateBaseModel();

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
        assertTrue(actual.contains("java.lang.IllegalArgumentException: Some error thrown."));
    }

}
