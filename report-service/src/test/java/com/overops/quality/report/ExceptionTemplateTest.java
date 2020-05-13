package com.overops.quality.report;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;

public class ExceptionTemplateTest extends AbstractTemplateTest {

    @Test
    public void testException() throws Exception {
        ReportVisualizationModel baseModel = getBaseModel();
        Template template = getHandlebars().compile("exception");

        Exception exception = null;
        try {
            InputStream j = null;
            Charset k = null;
            IOUtils.toString(j, k);
            
            // This never happens, but prevents a case where the IDE mistakenly think
            // exception object can actually be null later.
            //
            // Also, on the off chance someone accidentally makes the previous code
            // not throw an exception, this will still allow the test to work as expected.
            // 
            throw new Exception("dummy");
        } catch(Exception e) {
            exception = e;
        }

        baseModel.setHasException(true);
        baseModel.setExceptionMessage(exception.getMessage()+"");
        baseModel.setException(exception);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        baseModel.setStackTrace(sw.toString());

        String expected = resourceToString("testException.html");
        String actual = template.apply(baseModel);

        assertContent(actual, expected);
        assertTrue(actual.contains(baseModel.getEmailMessage()));
        assertTrue(actual.contains(exception.getMessage()+""));
        assertTrue(actual.contains("java.lang.NullPointerException"));
    }

}
