package com.overops.quality.report;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.overops.report.service.model.ReportVisualizationModel;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractTemplateTest {

    private static Handlebars handlebars;
    private ReportVisualizationModel baseModel;

    @BeforeAll
    public static void beforeClass() {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/web/template");
        loader.setSuffix(".hbs");

        handlebars = new Handlebars(loader);
        handlebars.registerHelper("or", ConditionalHelpers.or);
        handlebars.registerHelper("svg", new SvgHelper());
        handlebars.prettyPrint(true);
    }

    @BeforeEach
    public void before() throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = resourceToString("baseModel.json");
        baseModel = gson.fromJson(json, ReportVisualizationModel.class);
    }

    /**
     * Do a none whitespace compare first; if there are differences compare with whitespace so it is easier to determine
     * where things are going wrong.
     * @param actual
     * @param expected
     */
    protected void assertContent(String actual, String expected) {
        try{
            assertEquals(removeWhiteSpaces(actual), removeWhiteSpaces(expected));
        }catch(Exception e){
            assertEquals(actual, expected);
        }
    }

    protected String removeWhiteSpaces(String string) {
        return string.replaceAll("\\s+", "");
    }

    protected String resourceToString(String resourceName) throws IOException {
        return IOUtils.toString(MainTemplateTest.class.getResource(resourceName), Charset.defaultCharset());
    }

    protected Handlebars getHandlebars(){
        return handlebars;
    }

    protected ReportVisualizationModel getBaseModel(){
        return baseModel;
    }
}
