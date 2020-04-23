package com.overops.report.service.model;

public class HtmlParts {
    private String css = "";
    private String html = "";

    public HtmlParts() {
    }

    public HtmlParts(String html, String css) {
        this.css = css;
        this.html = html;
    }

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
    
}