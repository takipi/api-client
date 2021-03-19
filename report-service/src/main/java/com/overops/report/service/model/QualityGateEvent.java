package com.overops.report.service.model;

import com.takipi.api.client.data.event.Location;
import com.takipi.api.client.util.cicd.OOReportEvent;

import java.util.List;

/**
 * Quality Gate Event
 * <p>
 * Describes the event that was detected
 */
public class QualityGateEvent {

    private String arcLink = "";
    private String type = "";
    private String applications = "";
    private String introducedBy = "";
    private String eventSummary = "";
    private String eventRate = "";
    private long hits;
    private String location;

    public QualityGateEvent() {
        // No arg constructor for object mapping.
    }

    public QualityGateEvent(OOReportEvent event) {
        this.arcLink = event.getARCLink();
        this.type = event.getType();
        this.introducedBy = event.getIntroducedBy();
        this.eventSummary = event.getEventSummary();
        this.eventRate = event.getEventRate();
        this.hits = event.getHits();
        this.applications = event.getApplications();
        if(event.getEvent() != null && event.getEvent().error_location != null){
            Location location = event.getEvent().error_location;
            this.location = location.prettified_name +":"+ location.original_line_number;
        }
    }

    //<editor-fold desc="Getters & Setters">
    public String getArcLink() {
        return arcLink;
    }

    public void setArcLink(String arcLink) {
        this.arcLink = arcLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplications() {
        return applications;
    }

    public void setApplications(String applications) {
        this.applications = applications;
    }

    public String getIntroducedBy() {
        return introducedBy;
    }

    public void setIntroducedBy(String introducedBy) {
        this.introducedBy = introducedBy;
    }

    public String getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(String eventSummary) {
        this.eventSummary = eventSummary;
    }

    public String getEventRate() {
        return eventRate;
    }

    public void setEventRate(String eventRate) {
        this.eventRate = eventRate;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //</editor-fold>
}
