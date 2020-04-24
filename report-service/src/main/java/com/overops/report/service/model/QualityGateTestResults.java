package com.overops.report.service.model;

import java.util.List;

public class QualityGateTestResults {
    private boolean passed = false;
    private String message = "";
    private long errorCount = 0;
    private List<QualityGateEvent> events = null;

    public QualityGateTestResults() {

    }

    public QualityGateTestResults(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<QualityGateEvent> getEvents() {
        return events;
    }

    public void setEvents(List<QualityGateEvent> events) {
        this.events = events;
        errorCount = events != null ? events.size() : 0;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        if (this.events != null) {
            throw new IllegalStateException("Can not set error count when event list is not null.");
        }
        this.errorCount = errorCount;
    }
}