package com.overops.report.service.model;

public enum TestType {
    NEW_EVENTS_TEST("New", "new-gate"),
    RESURFACED_EVENTS_TEST("Resurfaced", "resurfaced-gate"),
    TOTAL_EVENTS_TEST("Total", "total-gate"),
    UNIQUE_EVENTS_TEST("Unique", "unique-gate"),
    CRITICAL_EVENTS_TEST("Critical", "critical-gate"),
    REGRESSION_EVENTS_TEST("Increasing", "increasing-gate");

    private String displayName;
    private String anchor;

    TestType(String displayName, String anchor) {
        this.displayName = displayName;
        this.anchor = anchor;
    }

    public String getDisplayName(){
        return this.displayName;
    }

    public String getAnchor(){
        return this.anchor;
    }
};