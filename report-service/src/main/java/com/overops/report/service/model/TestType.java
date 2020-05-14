package com.overops.report.service.model;

/**
 * Test Type
 * <p>
 * This enum is used during rendering time to help break out sections / types of quality gates
 */
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

    //<editor-fold desc="Getters">
    public String getDisplayName() {
        return this.displayName;
    }

    public String getAnchor() {
        return this.anchor;
    }
    //</editor-fold>
};