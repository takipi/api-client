package com.overops.quality.report;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;
import org.junit.jupiter.api.Test;

public class StyleTemplateTest extends AbstractTemplateTest {

    @Test
    public void testStyle() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        Template template = getHandlebars().compile("style");
        String expected = resourceToString("testStyle.html");
        String actual = template.apply(model);
        assertContent(actual, expected);
    }

}
