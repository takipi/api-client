package com.overops.quality.report;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;
import org.junit.jupiter.api.Test;

public class HeaderTemplateTest extends AbstractTemplateTest {

    @Test
    public void testHeader() throws Exception {
        ReportVisualizationModel baseModel = getBaseModel();
        Template template = getHandlebars().compile("header");
        String expected = resourceToString("testHeader.html");
        String actual = template.apply(baseModel);
        assertContent(actual, expected);
    }

}
