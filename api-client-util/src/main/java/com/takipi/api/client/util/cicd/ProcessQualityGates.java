package com.takipi.api.client.util.cicd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.event.EventUtil;
import com.takipi.api.client.util.regression.DeploymentsTimespan;
import com.takipi.api.client.util.regression.RateRegression;
import com.takipi.api.client.util.regression.RegressionInput;
import com.takipi.api.client.util.regression.RegressionStringUtil;
import com.takipi.api.client.util.regression.RegressionUtil;
import com.takipi.common.util.Pair;

public class ProcessQualityGates {

	private static QualityGateReport qualityReport;

	// process CICD gates based on the inputs sent in
	public static QualityGateReport processCICDInputs(ApiClient apiClient, RegressionInput input, boolean newEvents,
			boolean resurfacedEvents, String regexFilter, int topIssuesVolume, boolean countGate,
			PrintStream printStream, boolean verbose) {

		qualityReport = new QualityGateReport();
		
		DeploymentsTimespan deploymentsTimespan = RegressionUtil.getDeploymentsTimespan(apiClient, input.serviceId, input.deployments);
		
		if ((deploymentsTimespan == null) ||
			(deploymentsTimespan.getActiveWindow() == null) ||
			(deploymentsTimespan.getActiveWindow().getFirst() == null)) {
			throw new IllegalStateException("Deployment name " + input.deployments
					+ " not found. Please ensure your collector and Jenkins configuration are pointing to the same enviornment.");
		}
		
		Pair<DateTime, DateTime> deploymentsActiveWindow = deploymentsTimespan.getActiveWindow();
		
		DateTime deploymentStart = deploymentsActiveWindow.getFirst();
		
		Collection<EventResult> events = RegressionUtil.getActiveEventVolume(apiClient, input, deploymentStart,
				printStream);

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

	private static List<OOReportEvent> getTopXEvents(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, int topIssuesVolume, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();

		for (EventResult event : events) {
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, null, arcLink);
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
			result.add(newEvent);
		}
		return result;
	}

	// find new errors from error list
	private static List<OOReportEvent> getNewErrors(ApiClient apiClient, RegressionInput input,
			Collection<EventResult> events, DateTime deploymentStart) {
		List<OOReportEvent> result = new ArrayList<OOReportEvent>();
		for (EventResult event : events) {
			if (!input.deployments.contains(event.introduced_by)) {
				continue;
			}
			String arcLink = getArcLink(apiClient, event.id, input, deploymentStart);
			OOReportEvent newEvent = new OOReportEvent(event, RegressionStringUtil.NEW_ISSUE, arcLink);
			result.add(newEvent);
		}
		return result;
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
				// now increment counters
				qualityReport.addToTotalErrorCount(event.stats.hits);
				qualityReport.addToUniqueErrorCount(1);
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
