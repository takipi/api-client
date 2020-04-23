package com.overops.report.service;

public class QualityReportParams {
    private String applicationName = "";
    private String deploymentName = "";
    private String serviceId = "";
    private String regexFilter = "";
    private boolean markUnstable = false;
    private int printTopIssues = 5;
    private boolean newEvents = false;
    private boolean resurfacedErrors = false;
    private int maxErrorVolume = 0;
    private int maxUniqueErrors = 0;
    private String criticalExceptionTypes = "";
    private boolean checkRegressionErrors;
    private String activeTimespan = "0";
    private String baselineTimespan = "0";
    private int minVolumeThreshold = 0;
    private double minErrorRateThreshold = 0d;
    private double regressionDelta = 0d;
    private double criticalRegressionDelta = 0d;
    private boolean applySeasonality = false;

    private boolean debug = false;
    private boolean errorSuccess = false;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;
    }

    public boolean isMarkUnstable() {
        return markUnstable;
    }

    public void setMarkUnstable(boolean markUnstable) {
        this.markUnstable = markUnstable;
    }

    public int getPrintTopIssues() {
        return printTopIssues;
    }

    public void setPrintTopIssues(int printTopIssues) {
        this.printTopIssues = printTopIssues;
    }

    public boolean isNewEvents() {
        return newEvents;
    }

    public void setNewEvents(boolean newEvents) {
        this.newEvents = newEvents;
    }

    public boolean isResurfacedErrors() {
        return resurfacedErrors;
    }

    public void setResurfacedErrors(boolean resurfacedErrors) {
        this.resurfacedErrors = resurfacedErrors;
    }

    public int getMaxErrorVolume() {
        return maxErrorVolume;
    }

    public void setMaxErrorVolume(int maxErrorVolume) {
        this.maxErrorVolume = maxErrorVolume;
    }

    public int getMaxUniqueErrors() {
        return maxUniqueErrors;
    }

    public void setMaxUniqueErrors(int maxUniqueErrors) {
        this.maxUniqueErrors = maxUniqueErrors;
    }

    public String getCriticalExceptionTypes() {
        return criticalExceptionTypes;
    }

    public void setCriticalExceptionTypes(String criticalExceptionTypes) {
        this.criticalExceptionTypes = criticalExceptionTypes;
    }

    public boolean isCheckRegressionErrors() {
        return checkRegressionErrors;
    }

    public void setCheckRegressionErrors(boolean checkRegressionErrors) {
        this.checkRegressionErrors = checkRegressionErrors;
    }

    public String getActiveTimespan() {
        return activeTimespan;
    }

    public void setActiveTimespan(String activeTimespan) {
        this.activeTimespan = activeTimespan;
    }

    public String getBaselineTimespan() {
        return baselineTimespan;
    }

    public void setBaselineTimespan(String baselineTimespan) {
        this.baselineTimespan = baselineTimespan;
    }

    public int getMinVolumeThreshold() {
        return minVolumeThreshold;
    }

    public void setMinVolumeThreshold(int minVolumeThreshold) {
        this.minVolumeThreshold = minVolumeThreshold;
    }

    public double getMinErrorRateThreshold() {
        return minErrorRateThreshold;
    }

    public void setMinErrorRateThreshold(double minErrorRateThreshold) {
        this.minErrorRateThreshold = minErrorRateThreshold;
    }

    public double getRegressionDelta() {
        return regressionDelta;
    }

    public void setRegressionDelta(double regressionDelta) {
        this.regressionDelta = regressionDelta;
    }

    public double getCriticalRegressionDelta() {
        return criticalRegressionDelta;
    }

    public void setCriticalRegressionDelta(double criticalRegressionDelta) {
        this.criticalRegressionDelta = criticalRegressionDelta;
    }

    public boolean isApplySeasonality() {
        return applySeasonality;
    }

    public void setApplySeasonality(boolean applySeasonality) {
        this.applySeasonality = applySeasonality;
    }

    @Deprecated
    public boolean isDebug() {
        return debug;
    }

    @Deprecated
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isErrorSuccess() {
        return errorSuccess;
    }

    public void setErrorSuccess(boolean errorSuccess) {
        this.errorSuccess = errorSuccess;
    } 
}
