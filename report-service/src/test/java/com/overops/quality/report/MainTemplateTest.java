package com.overops.quality.report;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;
import org.junit.jupiter.api.Test;

public class MainTemplateTest extends AbstractTemplateTest {



    /**
     * Tests as if an empty model were passed in
     *
     * @throws Exception
     */
    @Test
    public void testDefaultModelReport() throws Exception {
        ReportVisualizationModel model = getBaseModel();
        Template template = getHandlebars().compile("main");
        String expected = resourceToString("testMain.html");
        String actual = template.apply(model);
        assertContent(actual, expected);
    }

}
