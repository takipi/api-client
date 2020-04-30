package com.overops.report.service.model;

import java.util.List;

public class QualityGateTestResults {
    private TestType testType;
    private boolean passed = false;
    private String message = "";
    private long errorCount = 0;
    private List<QualityGateEvent> events = null;

    public enum TestType {
        NEW_EVENTS_TEST, 
        RESURFACED_EVENTS_TEST, 
        TOTAL_EVENTS_TEST,
        UNIQUE_EVENTS_TEST, 
        CRITICAL_EVENTS_TEST,
        REGRESSION_EVENTS_TEST
    };

    public QualityGateTestResults(TestType testType) {
        this.testType = testType;
    }

    public QualityGateTestResults(TestType testType, boolean passed, String message) {
        this.testType = testType;
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

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }
}