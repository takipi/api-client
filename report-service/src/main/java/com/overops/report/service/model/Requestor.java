package com.overops.report.service.model;

/**
 * Requestor
 *
 * This enum ID represents the integration using the ARC screen links.  This is used for analytics / marketing.  This
 * list should be in sync with Confluence (https://overopshq.atlassian.net/wiki/spaces/PP/pages/1529872385/Hit+Sources)
 * and there is a JIRA ticket to clean this up (https://overopshq.atlassian.net/browse/OO-10236)
 */
public enum Requestor {
    UNKNOWN(100),
    GIT_LAB(80),
    TEAM_CITY(58),
    BAMBOO(52),
    CONCOURSE(47),
    JENKINS(4);

    private final int id;

    Requestor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}