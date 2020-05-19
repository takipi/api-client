package com.overops.report.service.model;

/**
 * HTML Model
 * <p>
 * This is used when embedding a report into existing templating engine (ie, JSP/Velocity/Jelly)
 * We found that it will be more consistent of we generically generate the HTML body and then jam it into the template
 * forced upon by the integration platform.
 */
public class HtmlParts {
    private String css = "";
    private String html = "";

    public HtmlParts() {
        // No arg constructor for object mapping.
    }

    public HtmlParts(String html, String css) {
        this.css = css;
        this.html = html;
    }

    //<editor-fold desc="Getters & Setters">
    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
    //</editor-fold>

}