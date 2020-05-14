package com.overops.report.service.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.overops.report.service.model.QualityGateTestResults.TestType;

public class QualityReport {

    public enum ReportStatus {
        PASSED,
        FAILED,
        WARNING
    }

    private enum WebResource {
        DANGER_ICON("web/img/embedded-danger.svg"),
        QUESTION_ICON("web/img/embedded-question.svg"),
        SUCCESS_ICON("web/img/embedded-success.svg"),
        TIMES_ICON("web/img/embedded-times.svg"),
        WARNING_ICON("web/img/embedded-warning.svg"),
        LOGO_ICON("web/img/overops-logo.svg"),
        CSS("web/css/style.css"),
        REPORT_HTML("web/html/report.hbs"),
        ERROR_GATE_HEADER_HTML("web/html/errorGateHeader.hbs"),
        ERROR_GATE_SUMMARY_HTML("web/html/errorGateSummary.hbs"),
        EVENT_DETAILS_HTML("web/html/eventDetails.html"),
        EVENTS_TABLE_HTML("web/html/eventsTable.html"),
        EXCEPTION_HTML("web/html/exception.hbs"),
        PAGE_HTML("web/html/page.hbs");

        private final String filePath;

        WebResource(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }
    }

    ReportStatus statusCode = ReportStatus.FAILED;
    String statusMsg = "";
    boolean showEventsForPassedTest;

    QualityGateTestResults newErrorsTestResults;
    QualityGateTestResults resurfacedErrorsTestResults;
    QualityGateTestResults criticalErrorsTestResults;
    QualityGateTestResults regressionErrorsTestResults;
    QualityGateTestResults totalErrorsTestResults;
    QualityGateTestResults uniqueErrorsTestResults;

    List<QualityGateEvent> topEvents = Collections.emptyList();

    QualityReportExceptionDetails exceptionDetails;

    private transient String[] webResources = new String[WebResource.values().length];

    public QualityReport() {
    }

    public ReportStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(ReportStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public QualityGateTestResults getNewErrorsTestResults() {
        return newErrorsTestResults;
    }

    public void setNewErrorsTestResults(QualityGateTestResults newErrorsTestResults) {
        this.newErrorsTestResults = newErrorsTestResults;
    }

    public QualityGateTestResults getResurfacedErrorsTestResults() {
        return resurfacedErrorsTestResults;
    }

    public void setResurfacedErrorsTestResults(QualityGateTestResults resurfacedErrorsTestResults) {
        this.resurfacedErrorsTestResults = resurfacedErrorsTestResults;
    }

    public QualityGateTestResults getCriticalErrorsTestResults() {
        return criticalErrorsTestResults;
    }

    public void setCriticalErrorsTestResults(QualityGateTestResults criticalErrorsTestResults) {
        this.criticalErrorsTestResults = criticalErrorsTestResults;
    }

    public QualityGateTestResults getRegressionErrorsTestResults() {
        return regressionErrorsTestResults;
    }

    public void setRegressionErrorsTestResults(QualityGateTestResults regressionErrorsTestResults) {
        this.regressionErrorsTestResults = regressionErrorsTestResults;
    }

    public QualityGateTestResults getTotalErrorsTestResults() {
        return totalErrorsTestResults;
    }

    public void setTotalErrorsTestResults(QualityGateTestResults totalErrorsTestResults) {
        this.totalErrorsTestResults = totalErrorsTestResults;
    }

    public QualityGateTestResults getUniqueErrorsTestResults() {
        return uniqueErrorsTestResults;
    }

    public void setUniqueErrorsTestResults(QualityGateTestResults uniqueErrorsTestResults) {
        this.uniqueErrorsTestResults = uniqueErrorsTestResults;
    }

    public List<QualityGateEvent> getTopEvents() {
        return topEvents;
    }

    public void setTopEvents(List<QualityGateEvent> topEvents) {
        this.topEvents = topEvents;
    }

    public HtmlParts getHtmlParts() {
        return this.getHtmlParts(false);
    }

    public HtmlParts getHtmlParts(boolean showEventsForPassedGates) {
        HtmlParts htmlParts = new HtmlParts();
        if (exceptionDetails != null) {
            htmlParts.setHtml(getExceptionHtml());
        } else {
            htmlParts.setHtml(getReportHtml(showEventsForPassedGates));
        }
        htmlParts.setCss(getWebResource(WebResource.CSS));
        return htmlParts;
    }

    public String toHtml() {
        return toHtml(false);
    }

    public String toHtml(boolean showEventsForPassedGates) {
        HtmlParts htmlParts = getHtmlParts(showEventsForPassedGates);
        String html = getWebResource(WebResource.PAGE_HTML).replace("<style></style>", "<style>" + htmlParts.getCss() + "</style>");
        html = html.replace("<body></body>", "<body>" + htmlParts.getHtml() + "</body>");
        return html;
    }

    private String getWebResource(WebResource resourceType) {
        String resource = "";
        if (resourceType != null) {
            resource = webResources[resourceType.ordinal()];
            if (resource == null) {
                try {
                    resource = readFile(resourceType.getFilePath());
                } catch (IOException e) {
                    resource = "";
                }
                webResources[resourceType.ordinal()] = resource;
            }
        }
        return resource;
    }

    private String readFile(String relativeFilePath) throws IOException {
        String fileContents = "";

        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(relativeFilePath);
        Reader reader = new InputStreamReader(stream);
        int intValueOfChar;
        while ((intValueOfChar = reader.read()) != -1) {
            fileContents += (char) intValueOfChar;
        }
        reader.close();
        return fileContents;
    }

    private String getExceptionHtml() {
        QualityReportGenerator generator = new QualityReportGenerator();
        return generator.generate(this, "exception");
    }

    private String getReportHtml(boolean showEventsForPassedGates) {
        QualityReportGenerator generator = new QualityReportGenerator();
        String reportHtml = generator.generate(this, "report");
       return reportHtml;
    }

    public QualityReportExceptionDetails getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(QualityReportExceptionDetails exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }

    public boolean isShowTopEvents(){
        try{
            if (!totalErrorsTestResults.isPassed() || !uniqueErrorsTestResults.isPassed()) {
                return true;
            } else if (showEventsForPassedTest && topEvents != null && topEvents.size() > 0) {
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException e){
            // TODO: This is a hack; fix me.
            return false;
        }
    }
}
