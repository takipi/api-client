package com.overops.quality.report;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.overops.report.service.model.QualityReport;

import java.io.IOException;

/**
 * Renders block if the status code matches.
 *
 * TODO:  Would be nice to make this generic switch/case/default that can be reusable
 */
public class ReportStatusHelper implements Helper<String> {

    @Override
    public Object apply(String path, Options options) throws IOException {
        QualityReport.ReportStatus reportStatus = options.get("statusCode");
        if(reportStatus.name().equals(path)){
            return options.fn(this);
        }else{
            return "";
        }
    }

}