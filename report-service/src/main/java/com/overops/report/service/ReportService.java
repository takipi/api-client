package com.overops.report.service;

import com.google.gson.Gson;
import com.overops.report.service.model.*;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.RemoteApiClient;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.cicd.OOReportEvent;
import com.takipi.api.client.util.cicd.ProcessQualityGates;
import com.takipi.api.client.util.cicd.QualityGateReport;
import com.takipi.api.client.util.regression.*;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.common.util.StringUtil;
import org.apache.http.client.utils.URIBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.overops.report.service.model.QualityReport.ReportStatus;
import static com.overops.report.service.model.QualityGateTestResults.TestType;

/**
 * Main Entry Point to Generate Reports
 * <p>
 * Current usage:
 * - GitLab
 * - Bamboo
 * - Concourse
 * - TeamCity
 * - Jenkins
 */
public class ReportService {

    private static final Logger LOG = LoggerFactory.getLogger(QualityReport.class);

    private static final String PLAN_HEAD = "<head><title>OverOps Error</title></head>";
    private static final String PLAN_BODY = "<body>An error occurred while generating a Quality Report.<br/>%s</body>";
    private static final String PLAN_TEMPLATE = "<!doctype html><html>" + PLAN_HEAD + PLAN_BODY + "</html>";

    private static final String REPORT_SERVICE = "quality-report";
    public static final Long DELAY_REPORT = 90L;
    public static final String URL_CREATION_TIMESTAMP = "urlCreationTimestamp";
    public static final String QUERY = "query";

    private transient Gson gson = new Gson();

    public String generateReportLink(String endPoint, QualityReportParams reportParams, PrintStream printStream, boolean debug) {
        try
        {
            validateLinkInputs(endPoint, reportParams, printStream, debug);
            URIBuilder uriBuilder = new URIBuilder(endPoint);
            uriBuilder.setPath(REPORT_SERVICE);
            String jsonReportParams = gson.toJson(reportParams);
            uriBuilder
                .addParameter(QUERY, jsonReportParams)
                .addParameter(URL_CREATION_TIMESTAMP, Long.toString(DateTime.now().getMillis() / 1000L));

            return uriBuilder.toString();
        }
        catch (URISyntaxException e)
        {
            throw new ReportGeneratorException("Unable to generate report link from URL: " + endPoint, e);
        }
    }

    public String generateReportLinkHtml(String endpoint, QualityReportParams reportParams, PrintStream printStream, boolean debug) {
        QualityReportLinkTemplate qualityReportLinkTemplate = new QualityReportLinkTemplate(generateReportLink(endpoint, reportParams, printStream, debug));
        return new QualityReportGenerator().generate(qualityReportLinkTemplate, "reportLink");
    }

    public String generateStillProcessingHtml(@SuppressWarnings("unused") String url, String creationTimestamp) {
        long delay = secondsLeftForQualityReport(creationTimestamp);
        return new QualityReportGenerator().generate(new QualityReportProcessingTemplate(delay), "processingReport");
    }

    /**
     * Get the time in seconds to delay the report otherwise 0.
     *
     * @param creationTimestamp the time the report was created
     * @return time in seconds to delay the report otherwise 0
     */
    public long secondsLeftForQualityReport(String creationTimestamp)
    {
        long now = DateTime.now().getMillis() / 1000L;
        long secondsDelay = Long.parseLong(creationTimestamp) + DELAY_REPORT - now;
        if (secondsDelay < 0) {
            secondsDelay = 0;
        }

        return secondsDelay;
    }

    /**
     * Determine if the quality report is ready to be displayed
     *
     * @param creationTimestamp timestamp to compare if enough time has elapsed
     * @return true if the report is ready otherwise false
     */
    public boolean isQualityReportReady(String creationTimestamp)
    {
        if (secondsLeftForQualityReport(creationTimestamp) == 0)
        {
            return true;
        }
        return false;
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, null, false).toHtml();
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor, PrintStream outputStream, boolean debug) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, outputStream, debug).toHtml();
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Integer requestorId, PrintStream outputStream, boolean debug) {
        return runQualityReport(endPoint, apiKey, reportParams, requestorId, outputStream, debug).toHtml();
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, null, false);
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor, PrintStream outputStream, boolean debug) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, outputStream, debug);
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Integer requestorId, PrintStream outputStream, boolean debug) {
        Integer actualRequestorId = ((requestorId != null) ? requestorId : Requestor.UNKNOWN.getId());

        try {
            boolean runRegressions = convertToMinutes(reportParams.getBaselineTimespan()) > 0;

            validateInputs(endPoint, apiKey, reportParams, outputStream, debug);

            RemoteApiClient apiClient = (RemoteApiClient) RemoteApiClient.newBuilder().setHostname(endPoint).setApiKey(apiKey).build();

            if (Objects.nonNull(outputStream) && debug) {
                apiClient.addObserver(new ApiClientObserver(outputStream, debug));
            }

            SummarizedView allEventsView = ViewUtil.getServiceViewByName(apiClient, reportParams.getServiceId().toUpperCase(), "All Events");

            if (Objects.isNull(allEventsView)) {
                throw new IllegalStateException(
                        "Could not acquire ID for 'All Events'. Please check connection to " + endPoint);
            }

            RegressionInput input = new RegressionInput();
            input.serviceId = reportParams.getServiceId();
            input.viewId = allEventsView.id;
            input.applictations = parseArrayString(reportParams.getApplicationName());
            input.deployments = parseArrayString(reportParams.getDeploymentName());
            input.criticalExceptionTypes = parseArrayString(reportParams.getCriticalExceptionTypes());

            if (runRegressions) {
                input.activeTimespan = convertToMinutes(reportParams.getActiveTimespan());
                input.baselineTime = reportParams.getBaselineTimespan();
                input.baselineTimespan = convertToMinutes(reportParams.getBaselineTimespan());
                input.minVolumeThreshold = reportParams.getMinVolumeThreshold();
                input.minErrorRateThreshold = reportParams.getMinErrorRateThreshold();
                input.regressionDelta = reportParams.getRegressionDelta();
                input.criticalRegressionDelta = reportParams.getCriticalRegressionDelta();
                input.applySeasonality = reportParams.isApplySeasonality();
                input.validate();
            }

            int maxEventVolume = reportParams.getMaxErrorVolume();
            int maxUniqueErrors = reportParams.getMaxUniqueErrors();
            boolean newEvents = reportParams.isNewEvents();
            boolean resurfacedEvents = reportParams.isResurfacedErrors();
            String regexFilter = reportParams.getRegexFilter();
            int topEventLimit = reportParams.getPrintTopIssues();

            //check if total or unique gates are being tested
            boolean countGate = false;
            if (maxEventVolume != 0 || maxUniqueErrors != 0) {
                countGate = true;
            }

            //get the CICD quality report for all gates but Regressions
            //initialize the QualityGateReport so we don't get null pointers below
            QualityGateReport qualityGateReport = new QualityGateReport();
            if (countGate || newEvents || resurfacedEvents || regexFilter != null) {
                qualityGateReport = ProcessQualityGates.processCICDInputs(apiClient, input, newEvents, resurfacedEvents,
                        regexFilter, topEventLimit, countGate, outputStream, debug);
            }

            //run the regression gate
            RateRegression rateRegression = null;
            List<OOReportRegressedEvent> regressions = null;
            if (runRegressions) {
                rateRegression = RegressionUtil.calculateRateRegressions(apiClient, input, outputStream, debug);

                List<OOReportEvent> topEvents = new ArrayList<>();
                Collection<EventResult> filter = null;

                Pattern pattern;

                if ((regexFilter != null) && (regexFilter.length() > 0)) {
                    pattern = Pattern.compile(regexFilter);
                } else {
                    pattern = null;
                }

                Collection<EventResult> eventsSet = filterEvents(rateRegression, pattern, outputStream, debug);
                List<EventResult> events = getSortedEventsByVolume(eventsSet);

                if (pattern != null) {
                    filter = eventsSet;
                }

                for (EventResult event : events) {
                    if (event.stats != null) {
                        if (topEvents.size() < topEventLimit) {
                            String arcLink = ProcessQualityGates.getArcLink(apiClient, event.id, input, rateRegression.getActiveWndowStart());
                            topEvents.add(new OOReportEvent(event, arcLink));
                        }
                    }
                }

                regressions = getAllRegressions(apiClient, input, rateRegression, filter);
                replaceSourceId(regressions, actualRequestorId);
            }

            replaceSourceId(qualityGateReport.getNewErrors(), actualRequestorId);
            replaceSourceId(qualityGateReport.getResurfacedErrors(), actualRequestorId);
            replaceSourceId(qualityGateReport.getCriticalErrors(), actualRequestorId);
            replaceSourceId(qualityGateReport.getTopErrors(), actualRequestorId);

            return runQualityReport(qualityGateReport, input, regressions, newEvents, resurfacedEvents, runRegressions, maxEventVolume, maxUniqueErrors, reportParams.isMarkUnstable());
        } catch (Throwable ex) {
            QualityReport qualityReport = new QualityReport();

            QualityReportExceptionDetails exceptionDetails = new QualityReportExceptionDetails();
            exceptionDetails.setExceptionMessage(ex.getMessage());

            List<StackTraceElement> stackElements = Arrays.asList(ex.getStackTrace());
            List<String> stackTrace = new ArrayList<>();
            stackTrace.add(ex.getClass().getName());
            stackTrace.addAll(stackElements.stream().map(stack -> stack.toString()).collect(Collectors.toList()));
            exceptionDetails.setStackTrace(stackTrace.toArray(new String[stackTrace.size()]));

            if (reportParams.isMarkUnstable()) {
                qualityReport.setStatusCode(ReportStatus.FAILED);
            } else {
                qualityReport.setStatusCode(ReportStatus.WARNING);
            }
            qualityReport.setExceptionDetails(exceptionDetails);

            return qualityReport;
        }
    }

    private int convertToMinutes(String timeWindow) {
        if (isEmptyString(timeWindow)) {
            return 0;
        }

        if (timeWindow.toLowerCase().contains("d")) {
            int days = Integer.parseInt(timeWindow.substring(0, timeWindow.indexOf("d")));
            return days * 24 * 60;
        } else if (timeWindow.toLowerCase().contains("h")) {
            int hours = Integer.parseInt(timeWindow.substring(0, timeWindow.indexOf("h")));
            return hours * 60;
        } else if (timeWindow.toLowerCase().contains("m")) {
            return Integer.parseInt(timeWindow.substring(0, timeWindow.indexOf("m")));
        }

        return 0;
    }

    private QualityReport runQualityReport(QualityGateReport qualityGateReport, RegressionInput input, List<OOReportRegressedEvent> regressions, boolean checkNewGate, boolean checkResurfacedGate, boolean checkRegressionGate, Integer maxEventVolume, Integer maxUniqueVolume, boolean markedUnstable) {
        QualityReport reportModel = new QualityReport();

        boolean checkCriticalGate = input.criticalExceptionTypes != null && (input.criticalExceptionTypes.size() > 0);

        boolean failedNewErrorGate = (qualityGateReport.getNewErrors() != null) && (qualityGateReport.getNewErrors().size() > 0);
        boolean failedResurfacedErrorGate = (qualityGateReport.getResurfacedErrors() != null) && (qualityGateReport.getResurfacedErrors().size() > 0);
        boolean failedCriticalErrorGate = (qualityGateReport.getCriticalErrors() != null) && (qualityGateReport.getCriticalErrors().size() > 0);
        boolean failedRegressionGate = (regressions != null) && (regressions.size() > 0);
        boolean failedTotalVolumeGate = (maxEventVolume != 0) && (qualityGateReport.getTotalErrorCount() >= maxEventVolume);
        boolean failedUniqueVolumeGate = (maxUniqueVolume != 0) && (qualityGateReport.getUniqueErrorCount() >= maxUniqueVolume);

        boolean unstable = failedRegressionGate || failedTotalVolumeGate || failedUniqueVolumeGate || failedNewErrorGate || failedResurfacedErrorGate || failedCriticalErrorGate;

        String deploymentName = getDeploymentName(input);
        if (!unstable) {
            reportModel.setStatusCode(ReportStatus.PASSED);
            if ((deploymentName != null) && (deploymentName.trim().length() > 0)) {
                reportModel.setStatusMsg("Congratulations, build " + deploymentName + " has passed all quality gates!");
            } else {
                reportModel.setStatusMsg("Congratulations, the build  has passed all quality gates!");
            }
        } else {
            if (markedUnstable) {
                reportModel.setStatusCode(ReportStatus.FAILED);
                if ((deploymentName != null) && (deploymentName.trim().length() > 0)) {
                    reportModel.setStatusMsg("OverOps has marked build " + deploymentName + " as \"failed\".");
                    ;
                } else {
                    reportModel.setStatusMsg("OverOps has marked the build as \"failed\".");
                    ;
                }
            } else {
                reportModel.setStatusCode(ReportStatus.WARNING);
                if ((deploymentName != null) && (deploymentName.trim().length() > 0)) {
                    reportModel.setStatusMsg("OverOps has detected issues with build " + deploymentName + " but did not mark the build as \"failed\".");
                } else {
                    reportModel.setStatusMsg("OverOps has detected issues with the build but did not mark the build as \"failed\".");
                }
            }
        }

        if (checkNewGate) {
            QualityGateTestResults newErrorsTestResults = new QualityGateTestResults(TestType.NEW_EVENTS_TEST);
            newErrorsTestResults.setPassed(!failedNewErrorGate);
            newErrorsTestResults.setEvents(qualityGateReport.getNewErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
            if (failedNewErrorGate) {
                newErrorsTestResults.setMessage("New Error Gate: Failed, OverOps detected " + newErrorsTestResults.getEvents().size() + " new error(s) in your build.");
            } else {
                newErrorsTestResults.setMessage("New Error Gate: Passed, OverOps did not detect any new errors in your build.");
            }
            reportModel.setNewErrorsTestResults(newErrorsTestResults);
        }

        if (checkCriticalGate) {
            QualityGateTestResults criticalErrorsTestResults = new QualityGateTestResults(TestType.CRITICAL_EVENTS_TEST);
            criticalErrorsTestResults.setPassed(!failedCriticalErrorGate);
            criticalErrorsTestResults.setEvents(qualityGateReport.getCriticalErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
            if (failedCriticalErrorGate) {
                criticalErrorsTestResults.setMessage("Critical Error Gate: Failed, OverOps detected " + criticalErrorsTestResults.getEvents().size() + " critical error(s) in your build.");
            } else {
                criticalErrorsTestResults.setMessage("Critical Error Gate: Passed, OverOps did not detect any critical errors in your build.");
            }
            reportModel.setCriticalErrorsTestResults(criticalErrorsTestResults);
        }

        if ((checkRegressionGate) &&
        	(regressions != null)) {
            String baselineTime = Objects.nonNull(input) ? input.baselineTime : "";

            QualityGateTestResults regressionErrorsTestResults = new QualityGateTestResults(TestType.REGRESSION_EVENTS_TEST);
            regressionErrorsTestResults.setPassed(!failedRegressionGate);
            regressionErrorsTestResults.setEvents(regressions.stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
            if (failedRegressionGate) {
                regressionErrorsTestResults.setMessage("Increasing Quality Gate: Failed, OverOps detected increasing errors in the current build against the baseline of " + baselineTime);
            } else {
                regressionErrorsTestResults.setMessage("Increasing Quality Gate: Passed, OverOps did not detect any increasing errors in the current build against the baseline of " + baselineTime);
            }
            reportModel.setRegressionErrorsTestResults(regressionErrorsTestResults);
        }

        if (checkResurfacedGate) {
            QualityGateTestResults resurfacedErrorsTestResults = new QualityGateTestResults(TestType.RESURFACED_EVENTS_TEST);
            resurfacedErrorsTestResults.setPassed(!failedResurfacedErrorGate);
            resurfacedErrorsTestResults.setEvents(qualityGateReport.getResurfacedErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
            if (failedResurfacedErrorGate) {
                resurfacedErrorsTestResults.setMessage("Resurfaced Error Gate: Failed, OverOps detected " + resurfacedErrorsTestResults.getEvents().size() + " resurfaced error(s) in your build.");
            } else {
                resurfacedErrorsTestResults.setMessage("Resurfaced Error Gate: Passed, OverOps did not detect any resurfaced errors in your build.");
            }
            reportModel.setResurfacedErrorsTestResults(resurfacedErrorsTestResults);
        }

        if (maxUniqueVolume != 0) {
            long uniqueEventsCount = qualityGateReport.getUniqueErrorCount();

            QualityGateTestResults uniqueErrorsTestResults = new QualityGateTestResults(TestType.UNIQUE_EVENTS_TEST);
            uniqueErrorsTestResults.setPassed(!failedUniqueVolumeGate);
            uniqueErrorsTestResults.setErrorCount(uniqueEventsCount);
            if (failedUniqueVolumeGate) {
                uniqueErrorsTestResults.setMessage("Unique Error Volume Gate: Failed, OverOps detected " + uniqueEventsCount + " unique error(s) which is >= the max allowable of " + maxUniqueVolume);
            } else {
                uniqueErrorsTestResults.setMessage("Unique Error Volume Gate: Passed, OverOps detected " + uniqueEventsCount + " unique error(s) which is < than max allowable of " + maxUniqueVolume);
            }
            reportModel.setUniqueErrorsTestResults(uniqueErrorsTestResults);
        }

        if (maxEventVolume != 0) {
            long eventVolume = qualityGateReport.getTotalErrorCount();
            QualityGateTestResults totalErrorsTestResults = new QualityGateTestResults(TestType.TOTAL_EVENTS_TEST);
            totalErrorsTestResults.setPassed(!failedTotalVolumeGate);
            totalErrorsTestResults.setErrorCount(eventVolume);
            if (failedTotalVolumeGate) {
                totalErrorsTestResults.setMessage("Total Error Volume Gate: Failed, OverOps detected " + eventVolume + " total error(s) which is >= the max allowable of " + maxEventVolume);
            } else {
                totalErrorsTestResults.setMessage("Total Error Volume Gate: Passed, OverOps detected " + eventVolume + " total error(s) which is < than max allowable of " + maxEventVolume);
            }
            reportModel.setTotalErrorsTestResults(totalErrorsTestResults);
        }

        if ((maxUniqueVolume != 0) || (maxEventVolume != 0)) {
            reportModel.setTopEvents(Optional.ofNullable(qualityGateReport.getTopErrors()).orElse(new ArrayList<>()).stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
        }

        return reportModel;
    }

    private String getDeploymentName(RegressionInput input) {
        if (Objects.nonNull(input) && Objects.nonNull(input.deployments)) {
            String value = input.deployments.toString();
            value = value.replace("[", "");
            value = value.replace("]", "");
            return value;
        }
        return "";
    }

    private void debugLinkInputs(String appUrl, QualityReportParams reportParams, PrintStream printStream, boolean debug)
    {
        if (debug && Objects.nonNull(printStream))
        {
            printStream.println("APP URL: " + appUrl);
            debugInputs(reportParams, printStream);
        }
    }

    private void debugInputs(String apiUrl, String apiKey, QualityReportParams reportParams, PrintStream printStream, boolean debug)
    {
        if (debug && Objects.nonNull(printStream))
        {
            printStream.println("API URL: " + apiUrl);
            String apiKeyMessage = StringUtil.isNullOrEmpty(apiKey) ? "is empty" : "is not empty";
            printStream.println("API Key: " + apiKeyMessage);
            debugInputs(reportParams, printStream);
        }
    }

    private void debugInputs(QualityReportParams reportParams, PrintStream printStream) {
        printStream.println("SID: " + reportParams.getServiceId());
        printStream.println("Application Name: " + reportParams.getApplicationName());
        printStream.println("Deployment Name: " + reportParams.getDeploymentName());
    }

    private void validateLinkInputs(String appUrl, QualityReportParams reportParams, PrintStream printStream, boolean debug)
    {
        debugLinkInputs(appUrl, reportParams, printStream, debug);

        if (isEmptyString(appUrl)) {
            throw new IllegalArgumentException("Missing APP URL");
        }

        validateInputs(reportParams);
    }

    private void validateInputs(String endPoint, String apiKey, QualityReportParams reportParams, PrintStream printStream, boolean debug) {
        debugInputs(endPoint, apiKey, reportParams, printStream, debug);

        if (isEmptyString(endPoint)) {
            throw new IllegalArgumentException("Missing host name");
        }

        if (isEmptyString(apiKey)) {
            throw new IllegalArgumentException("Missing api key");
        }

        validateInputs(reportParams);
    }

    private void validateInputs(QualityReportParams reportParams)
    {
        if (reportParams.isCheckRegressionErrors()) {
            if (!"0".equalsIgnoreCase(reportParams.getActiveTimespan())) {
                if (convertToMinutes(reportParams.getActiveTimespan()) == 0) {
                    throw new IllegalArgumentException("For Increasing Error Gate, the active timewindow currently set to: " + reportParams.getActiveTimespan() + " is not properly formated. See help for format instructions.");
                }
            }
            if (!"0".equalsIgnoreCase(reportParams.getBaselineTimespan())) {
                if (convertToMinutes(reportParams.getBaselineTimespan()) == 0) {
                    throw new IllegalArgumentException("For Increasing Error Gate, the baseline timewindow currently set to: " + reportParams.getBaselineTimespan() + " cannot be zero or is improperly formated. See help for format instructions.");
                }
            }
        }

        if (isEmptyString(reportParams.getServiceId())) {
            throw new IllegalArgumentException("Missing environment Id");
        }
    }

    private Collection<String> parseArrayString(String value) {
        if (isEmptyString(value)) {
            return Collections.emptySet();
        }

        return Arrays.asList(value.trim().split(Pattern.quote(",")));
    }

    private Collection<EventResult> filterEvents(RateRegression rateRegression, Pattern pattern, PrintStream output, boolean verbose) {
        Set<EventResult> result = new HashSet<>();
        if (pattern != null) {

            for (EventResult event : rateRegression.getNonRegressions()) {
                addEvent(result, event, pattern, output, verbose);
            }

            for (EventResult event : rateRegression.getAllNewEvents().values()) {
                addEvent(result, event, pattern, output, verbose);
            }

            for (RegressionResult regressionResult : rateRegression.getAllRegressions().values()) {
                addEvent(result, regressionResult.getEvent(), pattern, output, verbose);

            }

        } else {
            result.addAll(rateRegression.getNonRegressions());
            result.addAll(rateRegression.getAllNewEvents().values());

            for (RegressionResult regressionResult : rateRegression.getAllRegressions().values()) {
                result.add(regressionResult.getEvent());
            }
        }

        return result;
    }

    private List<EventResult> getSortedEventsByVolume(Collection<EventResult> events) {
        List<EventResult> result = new ArrayList<EventResult>(events);

        result.sort((o1, o2) -> {
            long v1;
            long v2;

            if (o1.stats != null) {
                v1 = o1.stats.hits;
            } else {
                v1 = 0;
            }

            if (o2.stats != null) {
                v2 = o2.stats.hits;
            } else {
                v2 = 0;
            }

            return (int) (v2 - v1);
        });

        return result;
    }

    private List<OOReportRegressedEvent> getAllRegressions(ApiClient apiClient, RegressionInput input, RateRegression rateRegression, Collection<EventResult> filter) {

        List<OOReportRegressedEvent> result = new ArrayList<OOReportRegressedEvent>();

        for (RegressionResult regressionResult : rateRegression.getCriticalRegressions().values()) {

            if ((filter != null) && (!filter.contains(regressionResult.getEvent()))) {
                continue;
            }

            String arcLink = ProcessQualityGates.getArcLink(apiClient, regressionResult.getEvent().id, input, rateRegression.getActiveWndowStart());

            OOReportRegressedEvent regressedEvent = new OOReportRegressedEvent(regressionResult.getEvent(), regressionResult.getBaselineHits(), regressionResult.getBaselineInvocations(), RegressionStringUtil.SEVERE_REGRESSION, arcLink);

            result.add(regressedEvent);
        }

        for (RegressionResult regressionResult : rateRegression.getAllRegressions().values()) {

            if (rateRegression.getCriticalRegressions().containsKey(regressionResult.getEvent().id)) {
                continue;
            }

            String arcLink = ProcessQualityGates.getArcLink(apiClient, regressionResult.getEvent().id, input, rateRegression.getActiveWndowStart());

            OOReportRegressedEvent regressedEvent = new OOReportRegressedEvent(regressionResult.getEvent(),
                    regressionResult.getBaselineHits(), regressionResult.getBaselineInvocations(), RegressionStringUtil.REGRESSION, arcLink);

            result.add(regressedEvent);
        }

        return result;
    }

    // for each event, replace the source ID in the ARC link with 58 (which means TeamCity)
    // see: https://overopshq.atlassian.net/wiki/spaces/PP/pages/1529872385/Hit+Sources
    private void replaceSourceId(List<? extends OOReportEvent> events, int requestorId) {
        if (events != null) {
            String match = "&source=(\\d)+";  // matches at least one digit
            String replace = "&source=" + requestorId;    // replace with 58

            for (OOReportEvent event : events) {
                String arcLink = event.getARCLink();
                if (arcLink != null) {
                    event.setArcLink(arcLink.replaceAll(match, replace));
                }
            }
        }
    }

    private boolean isEmptyString(String aString) {
        return (aString == null) || (aString.trim().length() == 0);
    }

    private void addEvent(Set<EventResult> events, EventResult event, Pattern pattern, PrintStream output, boolean verbose) {
        if (allowEvent(event, pattern)) {
            events.add(event);
        } else if ((output != null) && (verbose)) {
            output.println(event + " did not match regexFilter and was skipped");
        }
    }

    private boolean allowEvent(EventResult event, Pattern pattern) {
        if (pattern == null) {
            return true;
        }

        String json = gson.toJson(event);
        boolean result = !pattern.matcher(json).find();

        return result;
    }

    /**
     * Creates an HTML page for errors even if there is an issue with the template engine.
     *
     * @param exception
     * @return
     */
    public String exceptionHtml(Exception exception) {
        try {
            // Write stacktrace to string for template
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);

            QualityReport qualityReport = new QualityReport();
            QualityReportExceptionDetails details = new QualityReportExceptionDetails();
            details.setExceptionMessage(exception.getMessage());
            details.setStackTrace(sw.toString().split("\n"));
            qualityReport.setExceptionDetails(details);

            return qualityReport.toHtml();
        } catch (Exception e) {
            LOG.error("Error creating report with template engine:", e);
            // (Rare Case) If for some reason there is an error at the template layer spit out plan text error.
            return String.format(PLAN_TEMPLATE, "Error: " + exception.getMessage());
        }
    }
    
    public static void pauseForTheCause(PrintStream printStream) {
        if (Objects.nonNull(printStream)) {
            printStream.println("Build Step: Starting OverOps Quality Gate....");
        }
        try {
            Thread.sleep(30000);
        } catch (Exception e) {
            if (Objects.nonNull(printStream)) {
                printStream.println("Can not hold the process.");
            }
        }
    }
    
    public static void pauseForTheCause() {
        pauseForTheCause(System.out);
    }
    
    /**
     * Requestor
     * <p>
     * This enum ID represents the integration using the ARC screen links.  This is used for analytics / marketing.  This
     * list should be in sync with Confluence (https://overopshq.atlassian.net/wiki/spaces/PP/pages/1529872385/Hit+Sources)
     * and there is a JIRA ticket to clean this up (https://overopshq.atlassian.net/browse/OO-10236)
     */
    public enum Requestor {
        UNKNOWN(100),
        QUALITY_REPORT_DIRECT(90),
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
}
