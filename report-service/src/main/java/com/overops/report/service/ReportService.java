package com.overops.report.service;

import com.google.gson.Gson;
import com.overops.report.service.model.OOReportRegressedEvent;
import com.overops.report.service.model.QualityReport;
import com.overops.report.service.model.QualityReportExceptionDetails;
import com.overops.report.service.model.ReportVisualizationModel;
import com.overops.report.service.model.QualityReport.ReportStatus;
import com.overops.report.service.model.QualityGateTestResults;
import com.overops.report.service.model.QualityGateEvent;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.RemoteApiClient;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.cicd.OOReportEvent;
import com.takipi.api.client.util.cicd.ProcessQualityGates;
import com.takipi.api.client.util.cicd.QualityGateReport;
import com.takipi.api.client.util.regression.*;
import com.takipi.api.client.util.view.ViewUtil;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReportService {
    public enum Requestor {
        UNKNOWN(100),
        GIT_LAB(80),
        TEAM_CITY(58),
        BAMBOO(52),
        JENKINS(4);

        private final int id;

        Requestor(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private transient Gson gson = new Gson();

    public static void main(String[] args) {
        String endPoint = "https://api.overops.com";
        String apiKey = "";

        QualityReportParams reportParams = new QualityReportParams();
        reportParams.setApplicationName("collector-server");
        reportParams.setDeploymentName("v4.44.3");
        reportParams.setServiceId("S37777");
        reportParams.setNewEvents(true);
        reportParams.setResurfacedErrors(true);

        try {
            QualityReport visualizationModel;
            visualizationModel = new ReportService().runQualityReport(endPoint, apiKey, reportParams, Requestor.TEAM_CITY);
            
            String html = visualizationModel.toHtml();
    
            BufferedWriter writer = new BufferedWriter(new FileWriter("myReport.html"));
            writer.write(html);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public ReportVisualizationModel createReportVisualizationModel(String endPoint, String apiKey, QualityReportParams reportParams) {
        QualityReport qualityReport = runQualityReport(endPoint, apiKey, reportParams, null);

        QualityGateTestResults newResults = qualityReport.getNewErrorsTestResults();
        QualityGateTestResults resurfacedResults = qualityReport.getNewErrorsTestResults();
        QualityGateTestResults criticalResults = qualityReport.getNewErrorsTestResults();
        QualityGateTestResults totalResults = qualityReport.getNewErrorsTestResults();
        QualityGateTestResults uniqueResults = qualityReport.getNewErrorsTestResults();
        QualityGateTestResults regressionResults = qualityReport.getNewErrorsTestResults();

        ReportVisualizationModel reportVisualizationModel = new ReportVisualizationModel();
        reportVisualizationModel.setUnstable((qualityReport.getStatusCode() == ReportStatus.WARNING) || (qualityReport.getStatusCode() == ReportStatus.FAILED));
        reportVisualizationModel.setMarkedUnstable(qualityReport.getStatusCode() == ReportStatus.FAILED);
        reportVisualizationModel.setPassedNewErrorGate((newResults != null) && newResults.isPassed());
        reportVisualizationModel.setCheckNewEvents(newResults != null);
        reportVisualizationModel.setPassedResurfacedErrorGate((resurfacedResults != null) && resurfacedResults.isPassed());
        reportVisualizationModel.setCheckResurfacedEvents(resurfacedResults != null);
        reportVisualizationModel.setPassedCriticalErrorGate((criticalResults != null) && criticalResults.isPassed());
        reportVisualizationModel.setCheckCriticalErrors(criticalResults != null);
        reportVisualizationModel.setPassedTotalErrorGate((totalResults != null) && totalResults.isPassed());
        reportVisualizationModel.setCheckTotalErrors(totalResults != null);
        reportVisualizationModel.setPassedUniqueErrorGate((uniqueResults != null) && uniqueResults.isPassed());
        reportVisualizationModel.setCheckUniqueErrors(uniqueResults != null);
        reportVisualizationModel.setPassedRegressedEvents((regressionResults != null) && regressionResults.isPassed());
        reportVisualizationModel.setCheckRegressedErrors(regressionResults != null);
        reportVisualizationModel.setHasTopErrors((uniqueResults != null && !uniqueResults.isPassed()) || (totalResults != null && !totalResults.isPassed()));

        reportVisualizationModel.setSummary(qualityReport.getStatusMsg());

        if (newResults != null) {
            reportVisualizationModel.setNewErrorSummary(newResults.getMessage());
            reportVisualizationModel.setNewEvents(newResults.getEvents());
            reportVisualizationModel.setNewGateTotal(newResults.getErrorCount());
        }

        if (resurfacedResults != null) {
            reportVisualizationModel.setResurfacedErrorSummary(resurfacedResults.getMessage());
            reportVisualizationModel.setResurfacedEvents(resurfacedResults.getEvents());
            reportVisualizationModel.setResurfacedGateTotal(resurfacedResults.getErrorCount());
        }

        if (criticalResults != null) {
            reportVisualizationModel.setCriticalErrorSummary(criticalResults.getMessage());
            reportVisualizationModel.setCriticalEvents(criticalResults.getEvents());
            reportVisualizationModel.setCriticalGateTotal(criticalResults.getErrorCount());
        }

        if (regressionResults != null) {
            reportVisualizationModel.setRegressionSummary(regressionResults.getMessage());
            reportVisualizationModel.setRegressedEvents(regressionResults.getEvents());
            reportVisualizationModel.setRegressionGateTotal(regressionResults.getErrorCount());
        }

        if (totalResults != null) {
            reportVisualizationModel.setTotalErrorSummary(totalResults.getMessage());
            reportVisualizationModel.setTotalGateTotal(totalResults.getErrorCount());
        }

        if (uniqueResults != null) {
            reportVisualizationModel.setUniqueErrorSummary(uniqueResults.getMessage());
            reportVisualizationModel.setUniqueGateTotal(uniqueResults.getErrorCount());
        }

        reportVisualizationModel.setTopEvents(qualityReport.getTopEvents());

        QualityReportExceptionDetails exceptionDetails = qualityReport.getExceptionDetails();
        reportVisualizationModel.setHasException(exceptionDetails != null);       
        if (reportVisualizationModel.isHasException()) {
            reportVisualizationModel.setExceptionMessage(exceptionDetails.getExceptionMessage());
            reportVisualizationModel.setEmailMessage(exceptionDetails.getEmailMessage());
            reportVisualizationModel.setStackTrace(String.join("\n", exceptionDetails.getStackTrace()));
        }

        return reportVisualizationModel;
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor) throws IOException {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, null, false).toHtml();
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor, PrintStream outputStream, boolean debug) throws IOException {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, outputStream, debug).toHtml();
    }

    public String getQualityReportHtml(String endPoint, String apiKey, QualityReportParams reportParams, Integer requestorId, PrintStream outputStream, boolean debug) throws IOException {
        return runQualityReport(endPoint, apiKey, reportParams, requestorId, outputStream, debug).toHtml();
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, null, false);
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Requestor requestor, PrintStream outputStream, boolean debug) {
        return runQualityReport(endPoint, apiKey, reportParams, requestor != null ? requestor.getId() : null, outputStream, debug);
    }

    public QualityReport runQualityReport(String endPoint, String apiKey, QualityReportParams reportParams, Integer requestorId, PrintStream outputStream, boolean debug) {
        if (requestorId == null) {
            requestorId = Requestor.UNKNOWN.getId();
        }
        try {
            boolean runRegressions = convertToMinutes(reportParams.getBaselineTimespan()) > 0;

            validateInputs(endPoint, apiKey, reportParams);

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
            input.applictations = parseArrayString(reportParams.getApplicationName(), "Application Name");
            input.deployments = parseArrayString(reportParams.getDeploymentName(), "Deployment Name");
            input.criticalExceptionTypes = parseArrayString(reportParams.getCriticalExceptionTypes(), "Critical Exception Types");

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
                replaceSourceId(regressions, requestorId);
            }

            replaceSourceId(qualityGateReport.getNewErrors(), requestorId);
            replaceSourceId(qualityGateReport.getResurfacedErrors(), requestorId);
            replaceSourceId(qualityGateReport.getCriticalErrors(), requestorId);
            replaceSourceId(qualityGateReport.getTopErrors(), requestorId);

            return runQualityReport(qualityGateReport, input, rateRegression, regressions, newEvents, resurfacedEvents, runRegressions, maxEventVolume, maxUniqueErrors, reportParams.isMarkUnstable());
        } catch (Throwable ex) {
            QualityReport qualityReport = new QualityReport();

            QualityReportExceptionDetails exceptionDetails = new QualityReportExceptionDetails();
            exceptionDetails.setExceptionMessage(ex.getMessage());

            List<StackTraceElement> stackElements = Arrays.asList(ex.getStackTrace());
            List<String> stackTrace = new ArrayList<>();
            stackTrace.add(ex.getClass().getName());
            stackTrace.addAll(stackElements.stream().map(stack -> stack.toString()).collect(Collectors.toList()));
            exceptionDetails.setStackTrace(stackTrace.toArray(new String[stackTrace.size()]));

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


    private QualityReport runQualityReport(QualityGateReport qualityGateReport, RegressionInput input, RateRegression regression, List<OOReportRegressedEvent> regressions, boolean checkNewGate, boolean checkResurfacedGate, boolean checkRegressionGate, Integer maxEventVolume, Integer maxUniqueVolume, boolean markedUnstable) {
        QualityReport reportModel = new QualityReport();

        boolean checkCriticalGate = input.criticalExceptionTypes != null && (input.criticalExceptionTypes.size() > 0);

        boolean hasNewErrors = (qualityGateReport.getNewErrors() != null) && (qualityGateReport.getNewErrors().size() > 0);
        boolean hasResurfacedErrors = (qualityGateReport.getResurfacedErrors() != null) && (qualityGateReport.getResurfacedErrors().size() > 0);
        boolean hasCriticalErrors = (qualityGateReport.getCriticalErrors() != null) && (qualityGateReport.getCriticalErrors().size() > 0);
        boolean hasRegressions = (regressions != null) && (regressions.size() > 0);
        boolean maxVolumeExceeded = (maxEventVolume != 0) && (qualityGateReport.getTotalErrorCount() >= maxEventVolume);
        boolean maxUniqueErrorsExceeded = (maxUniqueVolume != 0) && (qualityGateReport.getUniqueErrorCount() >= maxUniqueVolume);
        
        boolean unstable = hasRegressions || maxVolumeExceeded || maxUniqueErrorsExceeded || hasNewErrors || hasResurfacedErrors || hasCriticalErrors;

        String deploymentName = getDeploymentName(input);
        if (!unstable) {
            reportModel.setStatusCode(ReportStatus.PASSED);
            reportModel.setStatusMsg("Congratulations, build " + deploymentName + " has passed all quality gates!");
        }  else {
            if (markedUnstable) {
                reportModel.setStatusCode(ReportStatus.FAILED);
                reportModel.setStatusMsg("OverOps has marked build "+ deploymentName + " as \"failure\"."); ;
            } else {
                reportModel.setStatusCode(ReportStatus.WARNING);
                reportModel.setStatusMsg("OverOps has detected issues with build "+ deploymentName + " but did not mark the build as \"failure\".");
            }
        }

        if (checkNewGate) {
            QualityGateTestResults newErrorsTestResults = new QualityGateTestResults();
            newErrorsTestResults.setPassed(!hasNewErrors);
            if (hasNewErrors) {
                newErrorsTestResults.setEvents(qualityGateReport.getNewErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
                newErrorsTestResults.setMessage("New Error Gate: Failed, OverOps detected " + newErrorsTestResults.getEvents().size() + " new error(s) in your build.");
            } else {
                newErrorsTestResults.setMessage("New Error Gate: Passed, OverOps did not detect any new errors in your build.");
            }
            reportModel.setNewErrorsTestResults(newErrorsTestResults);
        }

        if (checkCriticalGate) {
            QualityGateTestResults criticalErrorsTestResults = new QualityGateTestResults();
            criticalErrorsTestResults.setPassed(!hasCriticalErrors);
            if (hasCriticalErrors) {
                criticalErrorsTestResults.setEvents(qualityGateReport.getCriticalErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
                criticalErrorsTestResults.setMessage("Critical Error Gate: Failed, OverOps detected " + criticalErrorsTestResults.getEvents().size() + " critical error(s) in your build.");
            } else {
                criticalErrorsTestResults.setMessage("Critical Error Gate: Passed, OverOps did not detect any critical errors in your build.");
            }
            reportModel.setCriticalErrorsTestResults(criticalErrorsTestResults);
        }

        if (checkRegressionGate) {
            String baselineTime = Objects.nonNull(input) ? input.baselineTime : "";

            QualityGateTestResults regressionErrorsTestResults = new QualityGateTestResults();
            regressionErrorsTestResults.setPassed(!hasRegressions);
            if (hasRegressions) {
                regressionErrorsTestResults.setEvents(regressions.stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
                regressionErrorsTestResults.setMessage("Increasing Quality Gate: Failed, OverOps detected increasing errors in the current build against the baseline of " + baselineTime);
            } else {
                regressionErrorsTestResults.setMessage("Increasing Quality Gate: Passed, OverOps did not detect any increasing errors in the current build against the baseline of " + baselineTime);
            }
            reportModel.setRegressionErrorsTestResults(regressionErrorsTestResults);
        }

        if (checkResurfacedGate) {
            QualityGateTestResults resurfacedErrorsTestResults = new QualityGateTestResults();
            resurfacedErrorsTestResults.setPassed(!hasResurfacedErrors);
            if (hasResurfacedErrors) {
                resurfacedErrorsTestResults.setEvents(qualityGateReport.getResurfacedErrors().stream().map(e -> new QualityGateEvent(e)).collect(Collectors.toList()));
                resurfacedErrorsTestResults.setMessage("Resurfaced Error Gate: Failed, OverOps detected " + resurfacedErrorsTestResults.getEvents().size() + " resurfaced error(s) in your build.");
            } else {
                resurfacedErrorsTestResults.setMessage("Resurfaced Error Gate: Passed, OverOps did not detect any resurfaced errors in your build.");
            }
            reportModel.setResurfacedErrorsTestResults(resurfacedErrorsTestResults);
        }

        if (maxUniqueVolume != 0) {
            long uniqueEventsCount = qualityGateReport.getUniqueErrorCount();

            QualityGateTestResults uniqueErrorsTestResults = new QualityGateTestResults();
            uniqueErrorsTestResults.setPassed(!maxUniqueErrorsExceeded);
            if (maxUniqueErrorsExceeded) {
                uniqueErrorsTestResults.setMessage("Unique Error Volume Gate: Failed, OverOps detected " + uniqueEventsCount + " unique error(s) which is >= the max allowable of " + maxUniqueVolume);
                uniqueErrorsTestResults.setErrorCount(uniqueEventsCount);
            } else {
                uniqueErrorsTestResults.setMessage("Unique Error Volume Gate: Passed, OverOps detected " + uniqueEventsCount + " unique error(s) which is < than max allowable of " + maxUniqueVolume);
            }
            reportModel.setUniqueErrorsTestResults(uniqueErrorsTestResults);
        }

        if (maxUniqueVolume != 0) {
            long eventVolume = qualityGateReport.getTotalErrorCount();
            QualityGateTestResults totalErrorsTestResults = new QualityGateTestResults();
            totalErrorsTestResults.setPassed(!maxVolumeExceeded);
            if (maxVolumeExceeded) {
                totalErrorsTestResults.setMessage("Total Error Volume Gate: Failed, OverOps detected " + eventVolume + " total error(s) which is >= the max allowable of " + maxEventVolume);
                totalErrorsTestResults.setErrorCount(eventVolume);
            } else {
                totalErrorsTestResults.setMessage("Total Error Volume Gate: Passed, OverOps detected " + eventVolume + " total error(s) which is < than max allowable of " + maxEventVolume);
            }
            reportModel.setTotalErrorsTestResults(totalErrorsTestResults);
        }

        if ((maxUniqueVolume != 0) || (maxUniqueVolume != 0)) {
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

    private void validateInputs(String endPoint, String apiKey, QualityReportParams reportParams) {
        if (isEmptyString(endPoint)) {
            throw new IllegalArgumentException("Missing host name");
        }

        if (isEmptyString(apiKey)) {
            throw new IllegalArgumentException("Missing api key");
        }

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

    private Collection<String> parseArrayString(String value, String name) {
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
                String arcLink = event.getARCLink().replaceAll(match, replace);
                event.setArcLink(arcLink);
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

}
