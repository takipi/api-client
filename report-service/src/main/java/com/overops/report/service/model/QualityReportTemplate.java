package com.overops.report.service.model;

/**
 * Template Model for Quality Report
 */
public class QualityReportTemplate {

    /**
     * Quality Report Data
     */
    private final QualityReport data;

    /**
     * Determines if we should show events (if exists) even though the quality gate has passed
     */
    private boolean showEventsForPassedGates;

    /**
     * Determine if we should add extra html and css for standalone quality report
     */
    private boolean isStandalone;

    public QualityReportTemplate(QualityReport data) {
        this.data = data;
    }

    public QualityReport getData() {
        return data;
    }

    //<editor-fold desc="Getters & Setters">
    public boolean isShowEventsForPassedGates() {
        return showEventsForPassedGates;
    }

    public void setShowEventsForPassedGates(boolean showEventsForPassedGates) {
        this.showEventsForPassedGates = showEventsForPassedGates;
    }

    public boolean isStandalone() {
        return isStandalone;
    }

    public void setIsStandalone(boolean isStandalone) {
        this.isStandalone = isStandalone;
    }
    //</editor-fold>

    /**
     * HBS 'getter' to determine if there are exceptions
     *
     * @return
     */
    public boolean isHasExceptions() {
        return data.getExceptionDetails() != null;
    }

    /**
     * HBS 'getter' to determine if we should show top events or not
     *
     * @return
     */
    public boolean isShowTopEvents() {
        try {
            if (!data.getTotalErrorsTestResults().isPassed() || !data.getUniqueErrorsTestResults().isPassed()) {
                return true;
            } else if (showEventsForPassedGates && data.getTopEvents() != null && data.getTopEvents().size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            // TODO: This is a hack; fix me.
            return false;
        }
    }

    /**
     * HBS 'getter' to determine if we should show top events or not
     *
     * @return
     */
    public boolean isRenderTotalEventsTable() {
        QualityGateTestResults testResults = data.getTotalErrorsTestResults();
        return (testResults != null) && (!testResults.isPassed() || (showEventsForPassedGates && (testResults.getErrorCount() > 0)));
    }

    /**
     * HBS 'getter' to determine if we should show top events or not
     *
     * @return
     */
    public boolean isRenderUniqueEventsTable() {
        QualityGateTestResults testResults = data.getUniqueErrorsTestResults();
        return (testResults != null) && (!testResults.isPassed() || (showEventsForPassedGates && (testResults.getErrorCount() > 0)));
    }

}
