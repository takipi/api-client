package com.overops.report.service.model;

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

    public String getStyle() {
        return style;
    }

    public String getSvg() {
        return svg;
    }
}
