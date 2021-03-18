package com.overops.report.service.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Model for Quality Report
 */
public class QualityReport {

    private ReportStatus statusCode = ReportStatus.FAILED;
    private String statusMsg = "";

    private QualityGateTestResults newErrorsTestResults;
    private QualityGateTestResults resurfacedErrorsTestResults;
    private QualityGateTestResults criticalErrorsTestResults;
    private QualityGateTestResults regressionErrorsTestResults;
    private QualityGateTestResults totalErrorsTestResults;
    private QualityGateTestResults uniqueErrorsTestResults;

    private List<QualityGateEvent> topEvents = Collections.emptyList();

    private QualityReportExceptionDetails exceptionDetails;

    public QualityReport() {
    }

    //<editor-fold desc="Getters & Setters">
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

    public QualityReportExceptionDetails getExceptionDetails() {
        return exceptionDetails;
    }

    public void setExceptionDetails(QualityReportExceptionDetails exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
    }
    //</editor-fold>

    /**
     * TODO (ccaspanello) Move this stuff out of the model and into the QualityReportGenerator class.
     */
    public HtmlParts getHtmlParts() {
        return this.getHtmlParts(false);
    }

    public HtmlParts getHtmlParts(boolean showEventsForPassedGates) {
        HtmlParts htmlParts = new HtmlParts();
        htmlParts.setHtml(getReportHtml(showEventsForPassedGates));
        htmlParts.setCss(getStyleHtml());
        return htmlParts;
    }

    public String toHtml() {
        return toHtml(false);
    }

    public String toHtml(boolean showEventsForPassedGates) {
        return toHtml(showEventsForPassedGates, false);
    }

    public String toHtml(boolean showEventsForPassedGates, boolean isStandalone) {
        QualityReportGenerator generator = new QualityReportGenerator();
        QualityReportTemplate template = new QualityReportTemplate(this);
        template.setShowEventsForPassedGates(showEventsForPassedGates);
        template.setIsStandalone(isStandalone);
        visitorToRemoveHidden(template);
        return generator.generate(template, "page");
    }

    private String getStyleHtml() {
        QualityReportGenerator generator = new QualityReportGenerator();
        QualityReportTemplate template = new QualityReportTemplate(this);
        return generator.generate(template, "style");
    }

    private String getReportHtml(boolean showEventsForPassedGates) {
        QualityReportGenerator generator = new QualityReportGenerator();
        QualityReportTemplate template = new QualityReportTemplate(this);
        template.setShowEventsForPassedGates(showEventsForPassedGates);
        visitorToRemoveHidden(template);
        return (exceptionDetails == null) ? generator.generate(template, "report") : generator.generate(template, "exception");
    }

    private void visitorToRemoveHidden(QualityReportTemplate template){
        removeHidden(template.getData().getNewErrorsTestResults());
        removeHidden(template.getData().getResurfacedErrorsTestResults());
        removeHidden(template.getData().getCriticalErrorsTestResults());
        removeHidden(template.getData().getRegressionErrorsTestResults());
        removeHidden(template.getData().getTotalErrorsTestResults());
        removeHidden(template.getData().getUniqueErrorsTestResults());
        removeHidden(template.getData().getTopEvents());
    }

    private void removeHidden(QualityGateTestResults results){
        if(results != null){
            removeHidden(results.getEvents());
            // This seems a little odd but there is a guard on the setErrorCount forcing us to reset events to update
            // the error count (not sure why we can't just call `.size()` on the list)
            results.setEvents(results.getEvents());
        }
    }

    private void removeHidden(List<QualityGateEvent> results){
        if(results != null){
            List<QualityGateEvent> toRemove = results.stream()
                    .filter(event->event.getLabels() != null && event.getLabels().contains("Archive"))
                    .collect(Collectors.toList());
            results.removeAll(toRemove);
        }
    }

    /**
     * Overall Report Status
     *
     * Used to display if it passed / failed / or if things went wrong but we are passing it anyway.
     */
    public enum ReportStatus {
        PASSED("alert-success", "web/img/embedded-success.svg"),
        FAILED("alert-danger", "web/img/embedded-danger.svg"),
        WARNING("alert-warning", "web/img/embedded-warning.svg");

        private final String style;
        private final String svg;

        ReportStatus(String style, String svg) {
            this.style = style;
            this.svg = svg;
        }

        //<editor-fold desc="Getters">
        public String getStyle() {
            return style;
        }

        public String getSvg() {
            return svg;
        }
        //</editor-fold>
    }

}
