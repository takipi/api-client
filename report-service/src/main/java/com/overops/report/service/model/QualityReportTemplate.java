package com.overops.report.service.model;

public class QualityReportTemplate {

    private final QualityReport data;

    private boolean showEventsForPassedGates;

    public QualityReportTemplate(QualityReport data){
        this.data = data;
    }

    public QualityReport getData() {
        return data;
    }

    public boolean isShowEventsForPassedGates() {
        return showEventsForPassedGates;
    }

    public void setShowEventsForPassedGates(boolean showEventsForPassedGates) {
        this.showEventsForPassedGates = showEventsForPassedGates;
    }

    public boolean isHasExceptions(){
        return data.getExceptionDetails() != null;
    }

    public boolean isShowTopEvents(){
        try{
            if (!data.getTotalErrorsTestResults().isPassed() || !data.getUniqueErrorsTestResults().isPassed()) {
                return true;
            } else if (showEventsForPassedGates && data.getTopEvents() != null && data.getTopEvents().size() > 0) {
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException e){
            // TODO: This is a hack; fix me.
            return false;
        }
    }
}
