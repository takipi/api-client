package com.overops.report.service.model;

import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.cicd.OOReportEvent;
import com.takipi.api.client.util.regression.RegressionStringUtil;

public class OOReportRegressedEvent extends OOReportEvent {

    private final long baselineHits;
    private final long baselineInvocations;

    public OOReportRegressedEvent(EventResult activeEvent, long baselineHits, long baselineInvocations, String type, String arcLink) {
        super(activeEvent, type, arcLink);
        this.baselineHits = baselineHits;
        this.baselineInvocations = baselineInvocations;
    }

    @Override
    public String getEventRate() {
        return RegressionStringUtil.getRegressedEventRate(getEvent(), baselineHits, baselineInvocations);
    }

}
