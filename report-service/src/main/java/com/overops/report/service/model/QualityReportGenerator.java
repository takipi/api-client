package com.overops.report.service.model;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.overops.quality.report.ReportGeneratorException;
import com.overops.quality.report.SvgHelper;

import java.io.IOException;


public class QualityReportGenerator {

    public String generate(QualityReport model, String templateName) {
        try {
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/web/html");
            loader.setSuffix(".hbs");
            Handlebars handlebars = new Handlebars(loader);
            handlebars.registerHelper("or", ConditionalHelpers.or);
            handlebars.registerHelper("svg", new SvgHelper());
            handlebars.prettyPrint(true);
            Template template = handlebars.compile(templateName);
            return template.apply(model);
        } catch (IOException e) {
            throw new ReportGeneratorException("Unexpected exception generating report.hbs.", e);
        }
    }

}
