package com.overops.quality.report;

import com.github.jknack.handlebars.Template;
import com.overops.report.service.model.ReportVisualizationModel;
import org.junit.jupiter.api.Test;

public class ReportTemplateTest extends AbstractTemplateTest {


    /**
     * Check Everything / Everything Passes
     *
     * @throws Exception
     */
    @Test
    public void testReport1() throws Exception {
        ReportVisualizationModel model = getBaseModel();

        // First Level True
        model.setCheckNewEvents(true);
        model.setCheckResurfacedEvents(true);
        model.setCheckTotalErrors(true);
        model.setCheckUniqueErrors(true);
        model.setCheckCriticalErrors(true);
        model.setCheckRegressedErrors(true);

        // Second Level True
        model.setPassedNewErrorGate(true);
        model.setPassedResurfacedErrorGate(true);
        model.setPassedTotalErrorGate(true);
        model.setPassedUniqueErrorGate(true);
        model.setPassedCriticalErrorGate(true);
        model.setPassedRegressedEvents(true);

        assertReportTemplate(model, "testReport1.html");
    }

    /**
     * Don't Check Anything / Doesn't matter if things pass or fail
     *
     * @throws Exception
     */
    @Test
    public void testReport2() throws Exception {
        ReportVisualizationModel model = getBaseModel();

        model.setHasTopErrors(false);

        // First Level True
        model.setCheckNewEvents(false);
        model.setCheckResurfacedEvents(false);
        model.setCheckTotalErrors(false);
        model.setCheckUniqueErrors(false);
        model.setCheckCriticalErrors(false);
        model.setCheckRegressedErrors(false);

        // Second Level True
        model.setPassedNewErrorGate(true);
        model.setPassedResurfacedErrorGate(true);
        model.setPassedTotalErrorGate(true);
        model.setPassedUniqueErrorGate(true);
        model.setPassedCriticalErrorGate(true);
        model.setPassedRegressedEvents(true);

        assertReportTemplate(model, "testReport2.html");

        // Second Level False - Shouldn't change template
        model.setPassedNewErrorGate(false);
        model.setPassedResurfacedErrorGate(false);
        model.setPassedTotalErrorGate(false);
        model.setPassedUniqueErrorGate(false);
        model.setPassedCriticalErrorGate(false);
        model.setPassedRegressedEvents(false);

        assertReportTemplate(model, "testReport2.html");
    }

    /**
     * Check Everything / Everything Fails
     *
     * @throws Exception
     */
    @Test
    public void testReport3() throws Exception {
        ReportVisualizationModel model = getBaseModel();

        model.setHasTopErrors(true);

        // First Level False
        model.setCheckNewEvents(true);
        model.setCheckResurfacedEvents(true);
        model.setCheckTotalErrors(true);
        model.setCheckUniqueErrors(true);
        model.setCheckCriticalErrors(true);
        model.setCheckRegressedErrors(true);

        // Second Level False
        model.setPassedNewErrorGate(false);
        model.setPassedResurfacedErrorGate(false);
        model.setPassedTotalErrorGate(false);
        model.setPassedUniqueErrorGate(false);
        model.setPassedCriticalErrorGate(false);
        model.setPassedRegressedEvents(false);

        assertReportTemplate(model, "testReport3.html");
    }

    /**
     * Check Everything / Everything Fails / Turn off has totals
     *
     * @throws Exception
     */
    @Test
    public void testReport4() throws Exception {
        ReportVisualizationModel model = getBaseModel();

        model.setHasTopErrors(false);

        // First Level False
        model.setCheckNewEvents(true);
        model.setCheckResurfacedEvents(true);
        model.setCheckTotalErrors(true);
        model.setCheckUniqueErrors(true);
        model.setCheckCriticalErrors(true);
        model.setCheckRegressedErrors(true);

        // Second Level False
        model.setPassedNewErrorGate(false);
        model.setPassedResurfacedErrorGate(false);
        model.setPassedTotalErrorGate(false);
        model.setPassedUniqueErrorGate(false);
        model.setPassedCriticalErrorGate(false);
        model.setPassedRegressedEvents(false);

        assertReportTemplate(model, "testReport4.html");
    }

    private void assertReportTemplate(ReportVisualizationModel model, String expectedResource) throws Exception {
        Template template = getHandlebars().compile("report");
        String expected = resourceToString(expectedResource);
        String actual = template.apply(model);
        assertContent(actual, expected);
    }
}
