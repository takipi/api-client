package com.overops.report.service.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractQualityReportTest {

    protected QualityReport generateBaseModel() throws Exception {
        String json = resourceToString("baseModel.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, QualityReport.class);
    }

    protected void assertEqualsWithoutWhiteSpace(String expected, String actual) {
        assertEquals(expected.replaceAll(" ", ""), actual.replaceAll(" ", ""));
    }

    protected String resourceToString(String resourceName) throws IOException {
        return IOUtils.toString(QualityReportTest.class.getResource(resourceName), Charset.defaultCharset());
    }

}
