package com.takipi.api.client.util.cicd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.event.EventUtil;
import com.takipi.api.client.util.regression.RateRegression;
import com.takipi.api.client.util.regression.RegressionInput;
import com.takipi.api.client.util.regression.RegressionStringUtil;
import com.takipi.api.client.util.regression.RegressionUtil;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class ProcessQualityGates {

	private static QualityGateReport qualityReport;

	// process CICD gates based on the inputs sent in
	public static QualityGateReport processCICDInputs(ApiClient apiClient, RegressionInput input, boolean newEvents,
			boolean resurfacedEvents, String regexFilter, int topIssuesVolume, boolean countGate,
			PrintStream printStream, @SuppressWarnings("unused") boolean verbose) {

		qualityReport = new QualityGateReport();
		
		Pair<DateTime, DateTime> deploymentsActiveWindow = null;
		if ((input.deployments == null) || (input.deployments.size() == 0)) {
			deploymentsActiveWindow = RegressionUtil.getDeploymentsActiveWindow(apiClient, input.serviceId);
		} else {
			deploymentsActiveWindow = RegressionUtil.getDeploymentsActiveWindow(apiClient, input.serviceId, input.deployments);
		}
		if ((deploymentsActiveWindow == null) || (deploymentsActiveWindow.getFirst() == null)) {
			if ((input.deployments == null) || (input.deployments.size() == 0)) {
				throw new IllegalStateException("Unable to determine active window for report.");
			} else {
				throw new IllegalStateException("Deployments " + Arrays.toString(input.deployments.toArray())
					+ " not found. Please ensure your collector and CI/CD configuration are pointing to the same environment.");
			}
		}
		
		DateTime deploymentStart = deploymentsActiveWindow.getFirst();
		
		Collection<EventResult> events = getEvents(apiClient, input, deploymentStart, printStream);

		// if we find events, process the quality gates
		if (events != null && events.size() > 0) {

			// filter out unneeded events set in plugin (regexFilter)
			events = filterEvents(events, regexFilter);

			// check count gates
			if (countGate) {
				events = RateRegression.getSortedNewEvents(events);
				qualityReport.setTopErrors(getTopXEvents(apiClient, input, events, topIssuesVolume, deploymentStart));
			}

			// find new Errors
			if (newEvents) {
				qualityReport.setNewErrors(getNewErrors(apiClient, input, events, deploymentStart));
			}

			// find resurfaced Errors
			if (resurfacedEvents) {
				qualityReport.setResurfacedErrors(getResurfacedErrors(apiClient, input, events, deploymentStart));
			}

			// find critical errors
			if (input.criticalExceptionTypes != null) {
				qualityReport.setCriticalErrors(getCriticalErrors(apiClient, input, events, deploymentStart));
			}
		}

		return qualityReport;
	}

    private static void filterHidden(Collection<EventResult> events) {
        if(events != null){
            List<EventResult> toRemove = events.stream()
                    .filter(e -> e.labels.contains("Archive"))
                    .collect(Collectors.toList());
            events.removeAll(toRemove);
        }
    }

	private static Collection<EventResult> getEvents(
			ApiClient apiClient, RegressionInput input, DateTime deploymentStart, PrintStream printStream) {
		
		Collection<EventResult> events = RegressionUtil.getActiveEventVolume(apiClient, input, deploymentStart, printStream);

		if (!CollectionUtil.safeIsEmpty(events)) {
		    filterHidden(events);
			return events;
		}
		
		// Try without the app filters.
		//
		events = RegressionUtil.getActiveEventVolume(apiClient, input, deploymentStart, printStream, true);
		
		if (CollectionUtil.safeIsEmpty(events) || CollectionUtil.safeIsEmpty(input.applictations)) {
			// Sadly, still no events.
			//
            filterHidden(events);
			return events;
		}
		
		// Now filter by app name for app tier
		//
		return filterByLabel(events, (List<String>)input.applictations);
	}
	
	// This is for app tiers.
	//
	private static List<EventResult> filterByLabel(Collection<EventResult> inputEvents, List<String> list) {
		//this will only find the first app by design. We may need to find multiple in the future
		String appName = list.get(0) + ".app";
		
		List<EventResult> result = new ArrayList<EventResult>();

		for (EventResult eventResult : inputEvents) {
			if (eventResult.labels != null && eventResult.labels.contains(appName)) {
				result.add(eventResult);
			}
		}
		
		if (CollectionUtil.safeIsEmpty(result)) {
			throw new IllegalStateException("Application(s) " + Arrays.toString(list.toArray())
			+ " not found. Please ensure Application names(s) provided are correct.");
		}
		filterHidden(result);
        return result;
	}
	
	private static List<OOReportEvent> getTopXEvents(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, int topIssuesVolume, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();

		for (EventResult event : events) {
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, null, arcLink);
			newEvent.setApplications(getAppNames(input, event));
			result.add(newEvent);

			if (result.size() == topIssuesVolume) {
				break;
			}
		}
		return result;
	}

	// find critical errors from error list
	private static List<OOReportEvent> getCriticalErrors(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();
		for (EventResult event : events) {
			if (!input.criticalExceptionTypes.contains(event.name)) {
				continue;
			}
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, null, arcLink);
			newEvent.setApplications(getAppNames(input, event));
			result.add(newEvent);
		}
		return result;
	}

	// find resurfaced errors from error list
	private static List<OOReportEvent> getResurfacedErrors(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();
		for (EventResult event : events) {
			if (!event.labels.contains(RegressionStringUtil.RESURFACED_ISSUE)) {
				continue;
			}
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, null, arcLink);
			newEvent.setApplications(getAppNames(input, event));
			result.add(newEvent);
		}
		return result;
	}

	// find new errors from error list
	private static List<OOReportEvent> getNewErrors(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();
		for (EventResult event : events) {
			if ((input.deployments != null) && (input.deployments.size() > 0) && !input.deployments.contains(event.introduced_by)) {
				continue;
			}
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, RegressionStringUtil.NEW_ISSUE, arcLink);
			newEvent.setApplications(getAppNames(input, event));
			result.add(newEvent);
		}
		return result;
	}
	
	private static String getAppNames(RegressionInput input, EventResult event) {
		String appName = null;
		
		if (input.applictations != null && !input.applictations.isEmpty()) {
			List<String> list = (List<String>)input.applictations;
			appName = list.get(0);
		} else {
			boolean firstEvent = true;
			List<String> labelList = event.labels;
			for (String string : labelList) {
				if (string.contains(".app")) {
					int endpointPoint = string.indexOf(".app");
					if (firstEvent) {
						appName = string.substring(0, endpointPoint);
					} else {
						appName = appName + ", " + string.substring(0, endpointPoint);
					}
				}
			}
		}
		
		return appName;
	}

	// get the arclink for the event
	public static String getArcLink(ApiClient apiClient, String eventId, RegressionInput input,
			DateTime activeWindowStart) {

		DateTime from = activeWindowStart.minusMinutes(input.baselineTimespan);

		String result = EventUtil.getEventRecentLinkDefault(apiClient, input.serviceId, eventId, from, DateTime.now(),
				input.applictations, input.servers, input.deployments, EventUtil.DEFAULT_PERIOD);

		return result;
	}

	// filter out events based on the pattern
	private static Collection<EventResult> filterEvents(Collection<EventResult> events, String regexFilter) {
		if (regexFilter == null) {
			return events;
		}

		List<EventResult> returnEvents = new ArrayList<EventResult>();
		for (EventResult event : events) {
			if (evaluateEvent(event, getPattern(regexFilter))) {
				returnEvents.add(event);
				// now increment counters if events are not hidden
                if(!event.labels.contains("Archive")){
                    qualityReport.addToTotalErrorCount(event.stats.hits);
                    qualityReport.addToUniqueErrorCount(1);
                }
			}
		}
		return returnEvents;
	}

	// check if event matches the pattern - returns true if matches
	private static boolean evaluateEvent(EventResult event, Pattern pattern) {

		if (pattern == null) {
			return true;
		}

		String json = new Gson().toJson(event);
		boolean result = !pattern.matcher(json).find();

		return result;
	}

	// setup regex pattern
	private static Pattern getPattern(String regexFilter) {
		Pattern pattern;

		if ((regexFilter != null) && (regexFilter.length() > 0)) {
			pattern = Pattern.compile(regexFilter);
		} else {
			pattern = null;
		}
		return pattern;
	}
}
