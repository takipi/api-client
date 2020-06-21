package com.overops.report.service.model;

/**
 * Quality Report Exception Details Model
 * <p>
 * If for some reason the report cannot be generated this model houses the details needed to display the error
 */
public class QualityReportExceptionDetails {

    private String exceptionMessage;
    private String[] stackTrace;
    private String emailMessage;

    //<editor-fold desc="Getters & Setters">
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }
    //</editor-fold>
}