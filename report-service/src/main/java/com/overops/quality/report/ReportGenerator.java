package com.overops.quality.report;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.overops.report.service.model.ReportVisualizationModel;

import java.io.IOException;

/**
 * Report Generator
 */
public class ReportGenerator {

    /**
     * Takes the data model and uses Handlebars Java to generate a single HTML page
     * <p>
     * Handlebar templates are located in resources/templates and are post-fixed with *.hbs.  The main entry-entry point
     * for this report is 'main.hbs'
     *
     * @param model
     * @return fully formatted HTML string
     */
    public String generate(ReportVisualizationModel model) {
        try {
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/web/template");
            loader.setSuffix(".hbs");
            Handlebars handlebars = new Handlebars(loader);
            handlebars.registerHelper("or", ConditionalHelpers.or);
            handlebars.registerHelper("svg", new SvgHelper());
            handlebars.prettyPrint(true);
            Template template = handlebars.compile("main");
            return template.apply(model);
        } catch (IOException e) {
            throw new ReportGeneratorException("Unexpected exception generating report.hbs.", e);
        }
    }

}
