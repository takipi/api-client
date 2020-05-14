package com.overops.report.service.model;

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
