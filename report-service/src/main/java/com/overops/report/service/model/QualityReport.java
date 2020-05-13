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
        REPORT_HTML("web/html/report.html"),
        ERROR_GATE_HEADER_HTML("web/html/errorGateHeader.html"),
        ERROR_GATE_SUMMARY_HTML("web/html/errorGateSummary.html"),
        EVENT_DETAILS_HTML("web/html/eventDetails.html"),
        EVENTS_TABLE_HTML("web/html/eventsTable.html"),
        EXCEPTION_HTML("web/html/exception.hbs"),
        PAGE_HTML("web/html/page.html");

        private final String filePath;

        WebResource(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return filePath;
        }
    }

    ;

    ReportStatus statusCode = ReportStatus.FAILED;
    String statusMsg = "";

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
        String reportHtml = getWebResource(WebResource.REPORT_HTML);
        reportHtml = reportHtml.replace("<img id=\"logo\"></img>", getWebResource(WebResource.LOGO_ICON));

        String cssClass = null;
        String icon = null;
        ReportStatus statusCode = getStatusCode();
        switch (statusCode) {
            case PASSED:
                cssClass = "alert-success";
                icon = getWebResource(WebResource.SUCCESS_ICON);
                break;
            case WARNING:
                cssClass = "alert-warning";
                icon = getWebResource(WebResource.WARNING_ICON);
                break;
            default:
                cssClass = "alert-danger";
                icon = getWebResource(WebResource.DANGER_ICON);
                break;
        }
        reportHtml = reportHtml.replace("report-status", cssClass);
        reportHtml = reportHtml.replace("<img id=\"reportStatusIcon\"></img>", icon);
        reportHtml = reportHtml.replace("<span id=\"reportStatusMsg\"></span>", getStatusMsg());

        reportHtml = reportHtml.replace("<tr id=\"newErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.NEW_EVENTS_TEST));
        reportHtml = reportHtml.replace("<tr id=\"resurfacedErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.RESURFACED_EVENTS_TEST));
        reportHtml = reportHtml.replace("<tr id=\"totalErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.TOTAL_EVENTS_TEST));
        reportHtml = reportHtml.replace("<tr id=\"uniqueErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.UNIQUE_EVENTS_TEST));
        reportHtml = reportHtml.replace("<tr id=\"criticalErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.CRITICAL_EVENTS_TEST));
        reportHtml = reportHtml.replace("<tr id=\"regressionErrorsSummary\"></tr>", getErrorGateSummaryHtml(TestType.REGRESSION_EVENTS_TEST));

        reportHtml = reportHtml.replace("<div id=\"newErrorsHeader\"></div>", getEventsHeader(TestType.NEW_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<table id=\"newErrorEvents\"></table>", getEventsTable(TestType.NEW_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<div id=\"resurfacedErrorsHeader\"></div>", getEventsHeader(TestType.RESURFACED_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<table id=\"resurfacedErrorEvents\"></table>", getEventsTable(TestType.RESURFACED_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<div id=\"totalErrorsHeader\"></div>", getEventsHeader(TestType.TOTAL_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<div id=\"uniqueErrorsHeader\"></div>", getEventsHeader(TestType.UNIQUE_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<table id=\"topErrorEvents\"></table>", getEventsTable(TestType.TOTAL_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<div id=\"criticalErrorsHeader\"></div>", getEventsHeader(TestType.CRITICAL_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<table id=\"criticalErrorEvents\"></table>", getEventsTable(TestType.CRITICAL_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<div id=\"regressionErrorsHeader\"></div>", getEventsHeader(TestType.REGRESSION_EVENTS_TEST, showEventsForPassedGates));
        reportHtml = reportHtml.replace("<table id=\"regressionErrorEvents\"></table>", getEventsTable(TestType.REGRESSION_EVENTS_TEST, showEventsForPassedGates));

        return reportHtml;
    }

    private String getErrorGateSummaryHtml(TestType testType) {
        String html = "";
        QualityGateTestResults testResults = null;

        boolean passed = false;
        String anchorRef = "";
        String anchorDesc = "";
        long errorCount = 0;
        String icon = "";

        switch (testType) {
            case NEW_EVENTS_TEST:
                testResults = getNewErrorsTestResults();
                anchorRef = "#new-gate";
                anchorDesc = "New";
                break;
            case CRITICAL_EVENTS_TEST:
                testResults = getCriticalErrorsTestResults();
                anchorRef = "#critical-gate";
                anchorDesc = "Critical";
                break;
            case REGRESSION_EVENTS_TEST:
                testResults = getRegressionErrorsTestResults();
                anchorRef = "#increasing-gate";
                anchorDesc = "Increasing";
                break;
            case RESURFACED_EVENTS_TEST:
                testResults = getResurfacedErrorsTestResults();
                anchorRef = "#resurfaced-gate";
                anchorDesc = "Resurfaced";
                break;
            case TOTAL_EVENTS_TEST:
                testResults = getTotalErrorsTestResults();
                anchorRef = "#total-gate";
                anchorDesc = "Total";
                break;
            case UNIQUE_EVENTS_TEST:
                testResults = getUniqueErrorsTestResults();
                anchorRef = "#unique-gate";
                anchorDesc = "Unique";
                break;
        }

        if (testResults != null) {
            passed = testResults.isPassed();
            errorCount = testResults.getErrorCount();
            icon = passed ? getWebResource(WebResource.SUCCESS_ICON) : getWebResource(WebResource.TIMES_ICON);

            html = getWebResource(WebResource.ERROR_GATE_SUMMARY_HTML);

            html = html.replace("Failed", passed ? "Passed" : "Failed");
            html = html.replace("0", Long.toString(errorCount));
            html = html.replace("anchor-ref", anchorRef);
            html = html.replace("anchor-desc", anchorDesc);
            html = html.replace("<img></img>", icon);
        }

        return html;
    }

    private String getEventsHeader(TestType testType, boolean showEventsForPassedGates) {
        String html = "";
        QualityGateTestResults testResults = null;

        String anchorRef = "";

        switch (testType) {
            case NEW_EVENTS_TEST:
                testResults = getNewErrorsTestResults();
                anchorRef = "new-gate";
                break;
            case CRITICAL_EVENTS_TEST:
                testResults = getCriticalErrorsTestResults();
                anchorRef = "critical-gate";
                break;
            case REGRESSION_EVENTS_TEST:
                testResults = getRegressionErrorsTestResults();
                anchorRef = "increasing-gate";
                break;
            case RESURFACED_EVENTS_TEST:
                testResults = getResurfacedErrorsTestResults();
                anchorRef = "resurfaced-gate";
                break;
            case TOTAL_EVENTS_TEST:
                testResults = getTotalErrorsTestResults();
                anchorRef = "total-gate";
                break;
            case UNIQUE_EVENTS_TEST:
                testResults = getUniqueErrorsTestResults();
                anchorRef = "unique-gate";
                break;
        }

        if (testResults != null) {

            html = getWebResource(WebResource.ERROR_GATE_HEADER_HTML);
            html = html.replace("anchor-ref", anchorRef);
            html = html.replace("summary", testResults.getMessage());

            if (testResults.isPassed()) {
                if ((testType == TestType.NEW_EVENTS_TEST) || (testType == TestType.RESURFACED_EVENTS_TEST) || (testType == TestType.REGRESSION_EVENTS_TEST)) {
                    html = html.replace("mb-2", "");
                }
                html = html.replace("<img></img>", getWebResource(WebResource.SUCCESS_ICON));
                if (!showEventsForPassedGates || (testResults.getErrorCount() == 0)) {
                    html += "<p class=\"ml-2 muted\">Nothing to report</p>";
                }
            } else {
                html = html.replace("<img></img>", getWebResource(WebResource.DANGER_ICON));
            }
        }
        return html;
    }

    private String getEventsTable(TestType testType, boolean showEventsForPassedGates) {
        String html = "";
        QualityGateTestResults testResults = null;
        List<QualityGateEvent> events = new ArrayList<>();
        long errorCount = 0;
        boolean testPassed = true;

        switch (testType) {
            case NEW_EVENTS_TEST:
                testResults = getNewErrorsTestResults();
                if (testResults != null) {
                    testPassed = testResults.isPassed();
                    events = testResults.getEvents();
                    errorCount = testResults.getErrorCount();
                }
                break;
            case CRITICAL_EVENTS_TEST:
                testResults = getCriticalErrorsTestResults();
                if (testResults != null) {
                    testPassed = testResults.isPassed();
                    events = testResults.getEvents();
                    errorCount = testResults.getErrorCount();
                }
                break;
            case REGRESSION_EVENTS_TEST:
                testResults = getRegressionErrorsTestResults();
                if (testResults != null) {
                    testPassed = testResults.isPassed();
                    events = testResults.getEvents();
                    errorCount = testResults.getErrorCount();
                }
                break;
            case RESURFACED_EVENTS_TEST:
                testResults = getResurfacedErrorsTestResults();
                if (testResults != null) {
                    testPassed = testResults.isPassed();
                    events = testResults.getEvents();
                    errorCount = testResults.getErrorCount();
                }
                break;
            case TOTAL_EVENTS_TEST:
            case UNIQUE_EVENTS_TEST:
                testResults = getTotalErrorsTestResults();
                if (testResults != null) {
                    testPassed = testResults.isPassed();
                    events = getTopEvents();
                    errorCount = testResults.getErrorCount();
                }
                testResults = getUniqueErrorsTestResults();
                if (testResults != null) {
                    testPassed &= testResults.isPassed();
                    events = getTopEvents();
                    errorCount += testResults.getErrorCount();
                }
                break;
        }

        if (!testPassed || (showEventsForPassedGates && (errorCount > 0))) {
            html = getWebResource(WebResource.EVENTS_TABLE_HTML);
            if (testType == TestType.REGRESSION_EVENTS_TEST) {
                html = html.replace("<th>Volume</th>", "<th>Volume / Rate</th>");
            } else {
                html = html.replace("<th>Type</th>", "");
            }
            String eventsHtml = "";
            if (events != null) {
                for (QualityGateEvent event : events) {
                    String eventHtml = getWebResource(WebResource.EVENT_DETAILS_HTML);
                    eventHtml = eventHtml.replace("arcLink", convertNullToString(event.getArcLink()));
                    eventHtml = eventHtml.replace("summary", convertNullToString(event.getEventSummary()));
                    eventHtml = eventHtml.replace("applications", convertNullToString(event.getApplications()));
                    eventHtml = eventHtml.replace("introducedBy", convertNullToString(event.getIntroducedBy()));
                    eventHtml = eventHtml.replace("eventRate", Long.toString(event.getHits()));
                    if (testType == TestType.REGRESSION_EVENTS_TEST) {
                        eventHtml = eventHtml.replace("eventType", convertNullToString(event.getType()));
                    } else {
                        eventHtml = eventHtml.replace("<td>eventType</td>", "");
                    }
                    eventsHtml += eventHtml;
                }
            }
            html = html.replace("<tr class=\"events\"></tr>", eventsHtml);
        }
        return html;
    }

    private String convertNullToString(String string) {
        return string == null ? "" : string;
    }

    public QualityReportExceptionDetails getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(QualityReportExceptionDetails exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }
}
