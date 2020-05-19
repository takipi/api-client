package com.overops.report.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SvgHelper implements Helper<String> {
    @Override
    public Object apply(String path, Options options) throws IOException {
        InputStream inputStream = SvgHelper.class.getClassLoader().getResourceAsStream(path);
        String svgContent = IOUtils.toString(inputStream, Charset.defaultCharset());
        return new Handlebars.SafeString(svgContent);
    }
}
