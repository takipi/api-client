package com.overops.quality.report;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;
import org.junit.jupiter.api.Test;

public class StatusTemplateTest extends AbstractTemplateTest {


    @Test
    public void testStatus1() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        model.setMarkedUnstable(true);
        model.setUnstable(true);
        assertStatusTemplate(model, "testStatus1.html");
    }

    @Test
    public void testStatus2() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        model.setMarkedUnstable(true);
        model.setUnstable(false);
        assertStatusTemplate(model, "testStatus2.html");
    }

    @Test
    public void testStatus3() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        model.setMarkedUnstable(false);
        model.setUnstable(true);
        assertStatusTemplate(model, "testStatus3.html");
    }

    @Test
    public void testStatus4() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        model.setMarkedUnstable(false);
        model.setUnstable(false);
        assertStatusTemplate(model, "testStatus4.html");
    }

    private void assertStatusTemplate(ReportVisualizationModel model, String expectedResource) throws Exception {
        Template template = getHandlebars().compile("status");
        String expected = resourceToString(expectedResource);
        String actual = template.apply(model);
        assertContent(actual, expected);
    }

}
