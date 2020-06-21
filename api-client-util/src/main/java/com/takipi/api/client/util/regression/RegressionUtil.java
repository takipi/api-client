package com.takipi.api.client.util.regression;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.deployment.SummarizedDeployment;
import com.takipi.api.client.data.event.BaseStats;
import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.data.metrics.Graph.GraphPoint;
import com.takipi.api.client.data.metrics.Graph.GraphPointContributor;
import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.request.event.EventsVolumeRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.util.client.ClientUtil;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;
import com.takipi.common.util.Pair;

public class RegressionUtil {

	public static final int POINT_FACTOR = 60;
	private static final int MAX_BASELINE_POINTS = 100;
	
	private static final double MAJOR_SPIKE_FACTOR = 0.7;
	private static final double MINOR_SPIKE_FACTOR = 0.35;

	private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

	public static class RegressionWindow {
		public DateTime activeWindowStart;
		public int activeTimespan;
		public boolean deploymentNotFound;
		
		public RegressionWindow() {
			
		}
		
		@Override
		public RegressionWindow clone() {
			
			RegressionWindow result = new RegressionWindow();
			
			result.activeWindowStart = this.activeWindowStart;
			result.activeTimespan = this.activeTimespan;
			result.deploymentNotFound = this.deploymentNotFound;
			
			return result;
		}
	}

	enum RegressionState {
		YES, NO, NO_DATA;
	}

	private static boolean hasOlderRelatedEvents(EventResult event, PrintStream printStream, boolean verbose) {
		if (CollectionUtil.safeIsEmpty(event.similar_event_ids)) {
			return false;
		}

		int eventId;

		try {
			eventId = Integer.parseInt(event.id);
		} catch (Exception e) {
			return false;
		}

		for (String similarId : event.similar_event_ids) {
			int similarEventId;

			try {
				similarEventId = Integer.parseInt(similarId);
			} catch (Exception e) {
				continue;
			}

			if (eventId > similarEventId) {
				if ((verbose) && (printStream != null)) {
					printStream
							.println("New event " + event.toString() + " has older similar event with ID " + similarId);
				}

				return true;
			}
		}

		return false;
	}

	private static String printEvent(EventResult event) {
		StringBuilder result = new StringBuilder();

		result.append("Event ");
		result.append(event.id);
		result.append(" ");
		result.append(event.summary);

		if (!event.summary.contains("in")) {
			result.append(" in ");
			String[] parts = event.error_location.class_name.split(Pattern.quote("."));

			if (parts.length <= 1) {
				result.append(event.error_location.class_name);
			} else {
				result.append(parts[parts.length - 1]);
			}

			result.append(".");
			result.append(event.error_location.method_name);
		}

		if (!CollectionUtil.safeIsEmpty(event.similar_event_ids)) {
			result.append("(");
			result.append(String.join(",", event.similar_event_ids));
			result.append(")");
		}

		return result.toString();
	}

	private static final DecimalFormat df = new DecimalFormat("#.000");

	private static String f(double d) {
		if (d % 1 == 0) {
			return String.valueOf((int) d);
		}

		String result = df.format(d);

		if (result.startsWith(".")) {
			return "0" + result;
		}

		return result;
	}

	private static Map<String, long[]> getPeriodVolumes(DateTime startTime, Graph baselineGraph, int activeTimespan,
			int baselineTimespan) {
		Map<String, long[]> result = new HashMap<>();

		DateTime baselineStart = startTime.minusMinutes(baselineTimespan);
		int timeWindows = baselineTimespan / activeTimespan;

		for (GraphPoint graphPoint : baselineGraph.points) {

			DateTime firstSeen = dateTimeFormatter.parseDateTime(graphPoint.time);

			if (firstSeen.isBefore(baselineStart)) {
				continue;
			}

			Minutes timeDelta = Minutes.minutesBetween(baselineStart, firstSeen);

			if (graphPoint.contributors == null) {
				continue;
			}

			for (GraphPointContributor gpc : graphPoint.contributors) {
				long[] timeWindowVolumes = result.get(gpc.id);

				if (timeWindowVolumes == null) {
					timeWindowVolumes = new long[timeWindows];
					result.put(gpc.id, timeWindowVolumes);
				}

				int minutes = timeDelta.getMinutes();
				int index = Math.min(minutes / activeTimespan, timeWindowVolumes.length - 1);

				timeWindowVolumes[index] += gpc.stats.hits;
			}
		}

		return result;
	}

	private static String ps(BaseStats stats) {
		if (stats == null) {
			return "(null)";
		}

		return "(" + stats.hits + "/" + stats.invocations + ")";
	}

	private static RegressionState processNewsIssueRegression(EventResult activeEvent, DateTime activeFrom,
			RegressionInput input, RateRegression.Builder rateRegression, PrintStream printStream, boolean verbose) {

		DateTime firstSeen = dateTimeFormatter.parseDateTime(activeEvent.first_seen);

		boolean isEventNew = firstSeen.isAfter(activeFrom);
		boolean hasOlderSmiliarEvent = hasOlderRelatedEvents(activeEvent, printStream, verbose);
		boolean isFromDep = (input.deployments == null) || (input.deployments.size() == 0)
				|| (input.deployments.contains(activeEvent.introduced_by));

		boolean isNew = (isEventNew) && (!hasOlderSmiliarEvent) && (isFromDep);

		if (isNew) {
			rateRegression.addNewEvent(activeEvent.id, activeEvent);

			// events types in the critical list are considered as new regardless of
			// threshold

			boolean isUncaught = activeEvent.type.equals("Uncaught Exception");

			boolean isCriticalEventType = (input.criticalExceptionTypes != null)
					&& (input.criticalExceptionTypes.contains(activeEvent.name));

			if ((isUncaught) || (isCriticalEventType)) {
				rateRegression.addCriticalNewEvent(activeEvent.id, activeEvent);

				if (printStream != null) {
					printStream
							.println(printEvent(activeEvent) + " is critical new event with " + activeEvent.stats.hits);
				}

				return RegressionState.YES;
			}
		}

		if ((activeEvent.stats == null) || (activeEvent.stats.hits == 0)) {

			if ((verbose) && (printStream != null)) {
				printStream.println("No stats " + ps(activeEvent.stats) + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return RegressionState.NO_DATA;
		}
		
		double activeEventRatio;
		double minVolumeThreshold = input.getEventMinThreshold(activeEvent);
		
		boolean rateExceeded;
		boolean volumeExceeeded = (minVolumeThreshold > 0) && (activeEvent.stats.hits > minVolumeThreshold);

		if (activeEvent.stats.invocations == 0) {

			if ((verbose) && (printStream != null)) {
				printStream.println("No inv " + ps(activeEvent.stats) + printEvent(activeEvent));
			}

			rateExceeded = true;
			activeEventRatio = 0;
		} else {
			activeEventRatio = ((double) activeEvent.stats.hits / (double) activeEvent.stats.invocations);
			double minErrorRateThreshold = input.getEventMinErrorRateThreshold(activeEvent);
			
			rateExceeded = (minErrorRateThreshold > 0) && (activeEventRatio > minErrorRateThreshold);
		}
		
		if ((!volumeExceeeded) || (!rateExceeded)) {

			if ((verbose) && (printStream != null)) {

				printStream.println("Min threshold " + ps(activeEvent.stats) + printEvent(activeEvent) + "fails "
						+ "hits" + activeEvent.stats.hits + "ratio: " + activeEventRatio);
			}

			if (!volumeExceeeded) {
				rateRegression.addNonRegressions(activeEvent);
				return RegressionState.NO_DATA;
			} else {
				return RegressionState.NO;
			}	
		}

		if (isNew) {
			rateRegression.addExceededNewEvent(activeEvent.id, activeEvent);

			if (printStream != null) {
				printStream.println(printEvent(activeEvent) + " is new with ER: " + activeEventRatio + " hits: "
						+ activeEvent.stats.hits);
			}

			return RegressionState.YES;
		}

		return RegressionState.NO;
	}

	private static SeasonlityResult calculateSeasonality(EventResult activeEvent, Map<String, long[]> periodVolumes) {
		long[] eventPeriodVolumes = periodVolumes.get(activeEvent.id);

		long largerVolumePeriod = -1;
		int largerVolumePriodIndex = -1;
		long halfVolumePeriods = 0;

		if (eventPeriodVolumes != null) {
			for (int index = 0; index < eventPeriodVolumes.length; index++) {

				long periodVolume = eventPeriodVolumes[index];

				if (periodVolume > activeEvent.stats.hits * MAJOR_SPIKE_FACTOR) {
					largerVolumePriodIndex = index;
					largerVolumePeriod = periodVolume;
					break;
				}

				if (periodVolume > activeEvent.stats.hits * MINOR_SPIKE_FACTOR) {
					halfVolumePeriods++;
				}
			}
		}

		return new SeasonlityResult(largerVolumePeriod, halfVolumePeriods, largerVolumePriodIndex);
	}

	private static boolean processVolumeRegression(EventResult activeEvent, RegressionInput input,
			Map<String, RegressionStats> regressionsStats, Map<String, long[]> periodVolumes, int activeTimespan,
			RateRegression.Builder rateRegression, PrintStream printStream, boolean verbose) {

		if (input.regressionDelta == 0) {
			return false;
		}

		RegressionStats regressionStats = regressionsStats.get(activeEvent.id);

		if (regressionStats == null) {
			if ((verbose) && (printStream != null)) {
				printStream.println("No regression stats " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return false;
		}

		if (activeEvent.stats.hits == 0) {
			
			if ((verbose) && (printStream != null)) {
				printStream.println("No active hit stats " + printEvent(activeEvent));
			}

			return false;
		}
		
		if (regressionStats.hits == 0) {
			
			if ((verbose) && (printStream != null)) {
				printStream.println("No baseline hit stats " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return false;
		}
		
		double eventRegressionDelta = input.getEventRegressionDelta(activeEvent);
		double eventCriticalRegressionDelta = input.getEventCriticalRegressionDelta(activeEvent);

		double normalizedBaselineVolume = (double) (regressionStats.hits) / (double) (input.baselineTimespan);
		double normalizedActiveVolume = (double) (activeEvent.stats.hits) / (double) (activeTimespan);
		
		double normalizedActiveInv;
		double normalizedBaselineInv;
		double invRateDelta;
		
		double volRateDelta = ((normalizedActiveVolume) / (normalizedBaselineVolume)) - 1;
			
		boolean isRegression;
		boolean isCriticalRegression;
		
		if ((activeEvent.stats.invocations == 0) || (regressionStats.invocations == 0)) {
			
			if ((verbose) && (printStream != null)) {
				printStream.println("No invocations avail, defaulting to hits calc " + printEvent(activeEvent));
			}
						
			isRegression = volRateDelta > eventRegressionDelta;
	
			if (eventCriticalRegressionDelta > 0) {
				isCriticalRegression = volRateDelta > eventCriticalRegressionDelta;
			} else {
				isCriticalRegression = false;
			}
			
			normalizedActiveInv = 0;
			normalizedBaselineInv = 0;
			invRateDelta = 0;	
		} else {
		
			normalizedBaselineInv = (double) (regressionStats.invocations) / (double) (input.baselineTimespan);
			normalizedActiveInv = (double) (activeEvent.stats.invocations) / (double) (activeTimespan);
			
			invRateDelta = ((normalizedActiveInv) / (normalizedBaselineInv)) - 1;
			isRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > eventRegressionDelta;
	
			if (eventCriticalRegressionDelta > 0) {
				isCriticalRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > eventCriticalRegressionDelta;
			} else {
				isCriticalRegression = false;
			}
		}

		if (!isRegression) {
			if ((verbose) && (printStream != null)) {
				printStream.println("Not regressed " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return false;
		}

		if (printStream != null) {
			printStream.println(printEvent(activeEvent) + " regression\nRelHit: " + f(normalizedBaselineVolume) + " -> "
					+ f(normalizedActiveVolume) + "\nRelInv: " + f(normalizedBaselineInv) + " -> "
					+ f(normalizedActiveInv) + "\nVolDel: " + f(volRateDelta) + ", InvDel " + f(invRateDelta)
					+ "\nhits: " + f(regressionStats.hits) + " -> " + f(activeEvent.stats.hits) + "\ninv: "
					+ f(regressionStats.invocations) + " -> " + f(activeEvent.stats.invocations));
		}

		if (input.applySeasonality) {

			SeasonlityResult seasonlityResult = calculateSeasonality(activeEvent, periodVolumes);

			if (seasonlityResult.majorSpike >= 0) {
				if (printStream != null) {
					printStream.println("Period " + seasonlityResult.makorSpikeIndex + " = "
							+ seasonlityResult.majorSpike + " > active volume. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return false;
			}

			if (seasonlityResult.minorSpikes >= 2) {
				if (printStream != null) {
					printStream.println(seasonlityResult.minorSpikes
							+ " periods > 50% active volume detected. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return false;
			}
		}

		rateRegression.addRegression(activeEvent.id, activeEvent, regressionStats.hits, regressionStats.invocations);

		if ((isCriticalRegression) && (input.criticalRegressionDelta > 0)) {
			rateRegression.addCriticalRegression(activeEvent.id, activeEvent, regressionStats.hits,
					regressionStats.invocations);
		}

		if (printStream != null) {
			printStream.println("\n");
		}

		return true;
	}

	private static void ApplyFilter(ViewTimeframeRequest.Builder builder, RegressionInput input, boolean applyDeps, boolean applyApps) {

		if ((applyApps) && (input.applictations != null)) {
			for (String app : input.applictations) {

				if (!app.isEmpty()) {
					builder.addApp(app);
				}
			}
		}

		if ((applyDeps) && (input.deployments != null)) {
			for (String dep : input.deployments) {
				if (!dep.isEmpty()) {
					builder.addDeployment(dep);
				}
			}
		}

		if (input.servers != null) {
			for (String srv : input.servers) {
				if (!srv.isEmpty()) {
					builder.addServer(srv);
				}
			}
		}
	}

	public static EventsResult getEventsVolume(ApiClient apiClient, RegressionInput input, DateTime from, DateTime to) {
		return getEventsVolume(apiClient, input, from, to, false);
	}
	
	public static EventsResult getEventsVolume(ApiClient apiClient, RegressionInput input, DateTime from, DateTime to, boolean ignoreAppsFilter) {

		String fromStr = from.toString(ISODateTimeFormat.dateTime().withZoneUTC());
		String toStr = to.toString(ISODateTimeFormat.dateTime().withZoneUTC());

		EventsVolumeRequest.Builder builder = EventsVolumeRequest.newBuilder().setServiceId(input.serviceId)
				.setViewId(input.viewId).setFrom(fromStr).setTo(toStr).setVolumeType(VolumeType.all);

		ApplyFilter(builder, input, true, !ignoreAppsFilter);

		Response<EventsResult> response = apiClient.get(builder.build());

		if (response.isBadResponse()) {
			throw new IllegalStateException("Error querying volume data with code " + response.responseCode);
		}

		EventsResult result = response.data;

		return result;
	}

	private static Graph validateGraph(ApiClient apiClient, GraphResult graphResult, RegressionInput input,
			PrintStream printStream) {

		if (CollectionUtil.safeIsEmpty(graphResult.graphs)) {
			if (printStream != null) {
				printStream.println("Empty graph result from " + apiClient.getHostname() + " for " + input.toString());
			}

			return null;
		}

		Graph graph = graphResult.graphs.get(0);

		if (!input.viewId.equals(graph.id)) {
			if (printStream != null) {
				printStream.println("Graph Id mismatch recevied " + graph.id + " from " + apiClient.getHostname()
						+ " for " + input.toString());
			}

			return null;
		}

		if (CollectionUtil.safeIsEmpty(graph.points)) {
			if (printStream != null) {
				printStream.println("Empty graph points from " + apiClient.getHostname() + " for " + input.toString());
			}

			return null;
		}

		return graph;
	}

	private static boolean validateVolume(ApiClient apiClient, EventsResult eventResult, RegressionInput input,
			PrintStream printStream) {

		if (CollectionUtil.safeIsEmpty(eventResult.events)) {
			if (printStream != null) {
				printStream.println(
						"Empty event volume result from " + apiClient.getHostname() + " for " + input.toString());
			}

			return false;
		}

		return true;
	}

	public static Pair<DateTime, DateTime> getDeploymentsActiveWindow(ApiClient apiClient, String serviceId) {
		
		// get *all* deployments, both active and inactive
		Collection<SummarizedDeployment> activeSummarizedDeployments = ClientUtil.getSummarizedDeployments(apiClient, serviceId, false);

		return getDeploymentsActiveWindow(activeSummarizedDeployments);
	}

	
	public static Pair<DateTime, DateTime> getDeploymentsActiveWindow(ApiClient apiClient, String serviceId, Collection<String> deployments) {
		
		// get *all* deployments, both active and inactive
		Collection<SummarizedDeployment> activeSummarizedDeployments = ClientUtil.getSummarizedDeployments(apiClient, serviceId, false);
		
		return getDeploymentsActiveWindow(deployments, activeSummarizedDeployments);
	}

	private static Pair<DateTime, DateTime> getDeploymentsActiveWindow(Collection<SummarizedDeployment> summarizedDeployments) {
		DateTime minDeploymentStart = null;
		DateTime maxDeploymentEnd = new DateTime(0L);

		for (SummarizedDeployment summarizedDeployment : summarizedDeployments) {

			if ((summarizedDeployment == null) || (summarizedDeployment.first_seen == null)) {
				return null;
			}

			DateTime start = dateTimeFormatter.parseDateTime(summarizedDeployment.first_seen);
			DateTime end = null;

			if (summarizedDeployment.last_seen != null) {
				end = dateTimeFormatter.parseDateTime(summarizedDeployment.last_seen);
			}

			if ((minDeploymentStart == null) || (start.isBefore(minDeploymentStart))) {

				minDeploymentStart = start;
			}

			if ((end == null) || (end.isAfter(maxDeploymentEnd))) {

				maxDeploymentEnd = end;
			}
		}

		Pair<DateTime, DateTime> deploymentsActiveWindow = Pair.of(minDeploymentStart, maxDeploymentEnd);

		return deploymentsActiveWindow;
	}
	
	private static Pair<DateTime, DateTime> getDeploymentsActiveWindow(Collection<String> deployments,
			Collection<SummarizedDeployment> summarizedDeployments) {

		Map<String, SummarizedDeployment> summarizedDeploymentByName = new HashMap<>();

		for (SummarizedDeployment summaryDeployment : summarizedDeployments) {
			summarizedDeploymentByName.put(summaryDeployment.name, summaryDeployment);
		}

		DateTime minDeploymentStart = null;
		DateTime maxDeploymentEnd = new DateTime(0L);

		for (String deployment : deployments) {
			SummarizedDeployment summarizedDeployment = summarizedDeploymentByName.get(deployment);

			if ((summarizedDeployment == null) || (summarizedDeployment.first_seen == null)) {
				return null;
			}

			DateTime start = dateTimeFormatter.parseDateTime(summarizedDeployment.first_seen);
			DateTime end = null;

			if (summarizedDeployment.last_seen != null) {
				end = dateTimeFormatter.parseDateTime(summarizedDeployment.last_seen);
			}

			if ((minDeploymentStart == null) || (start.isBefore(minDeploymentStart))) {

				minDeploymentStart = start;
			}

			if ((end == null) || (end.isAfter(maxDeploymentEnd))) {

				maxDeploymentEnd = end;
			}
		}

		Pair<DateTime, DateTime> deploymentsActiveWindow = Pair.of(minDeploymentStart, maxDeploymentEnd);

		return deploymentsActiveWindow;
	}

	public static RegressionWindow getActiveWindow(ApiClient apiClient, RegressionInput input,
			Collection<SummarizedDeployment> summarizedDeployments, PrintStream printStream) {
		RegressionWindow result = new RegressionWindow();

		result.activeTimespan = input.activeTimespan;

		if (input.activeWindowStart != null) {
			result.activeWindowStart = input.activeWindowStart;

			return result;
		}

		DateTime now = DateTime.now();

		if (CollectionUtil.safeIsEmpty(input.deployments)) {
			result.activeWindowStart = now.minusMinutes(input.activeTimespan);

			return result;
		}
		
		Pair<DateTime, DateTime> deploymentsActiveWindow;
		
		if (CollectionUtil.safeIsEmpty(summarizedDeployments)) {
			deploymentsActiveWindow = getDeploymentsActiveWindow(apiClient, input.serviceId, input.deployments);
		} else {
			deploymentsActiveWindow = getDeploymentsActiveWindow(input.deployments, summarizedDeployments);
		}

		if (deploymentsActiveWindow == null) {
			result.activeWindowStart = now.minusMinutes(input.activeTimespan);
			result.deploymentNotFound = true;
			return result;
		}

		result.activeWindowStart = deploymentsActiveWindow.getFirst();

		if (result.activeWindowStart == null) {
			
			result.deploymentNotFound = true;
			
			if (printStream != null) {
				printStream.println(
						"Could not acquire start time for deployments " + Arrays.toString(input.deployments.toArray()));
			}

			return result;
		}

		DateTime activeWindowEnd;

		if (deploymentsActiveWindow.getSecond() != null) {
			activeWindowEnd = deploymentsActiveWindow.getSecond();
		} else {
			activeWindowEnd = now;
		}

		result.activeTimespan = (int) TimeUnit.MILLISECONDS
				.toMinutes(activeWindowEnd.minus(result.activeWindowStart.getMillis()).getMillis());

		if (result.activeTimespan <= 0) {
			result.activeTimespan = (int) (TimeUnit.DAYS.toMinutes(1));
			result.activeWindowStart = now.minusDays(1);
		}

		return result;
	}

	private static Graph getBaselineGraph(ApiClient apiClient, RegressionInput input, DateTime baselineStart,
			DateTime activeWindowStart, int activeTimespan, PrintStream printStream) {

		Graph result = null;

		if (input.baselineGraph != null) {
			result = input.baselineGraph;
		} else {
			int pointsWanted = Math.min(input.baselineTimespan / activeTimespan * 2, MAX_BASELINE_POINTS);

			if (pointsWanted > 0) {
				GraphResult graphResult = ViewUtil.getEventsGraphResult(apiClient, input.serviceId, input.viewId,
						pointsWanted, VolumeType.all, baselineStart, activeWindowStart, true, true, true, true);

				if (graphResult != null) {
					result = validateGraph(apiClient, graphResult, input, printStream);
				}
			}
		}

		return result;
	}

	public static Collection<EventResult> getActiveEventVolume(ApiClient apiClient, RegressionInput input,
			DateTime activeWindowStart, PrintStream printStream) {
		return getActiveEventVolume(apiClient, input, activeWindowStart, printStream, false);
	}
	
	public static Collection<EventResult> getActiveEventVolume(ApiClient apiClient, RegressionInput input,
			DateTime activeWindowStart, PrintStream printStream, boolean ignoreAppsFilter) {

		Collection<EventResult> result;

		if (input.events != null) {
			result = input.events;
		} else {

			EventsResult activeEventVolume = getEventsVolume(apiClient, input, activeWindowStart, DateTime.now(), ignoreAppsFilter);

			if (!validateVolume(apiClient, activeEventVolume, input, printStream)) {
				return null;
			}

			result = activeEventVolume.events;
		}

		return result;
	}
	
	public static RateRegression calculateRateRegressions(ApiClient apiClient, RegressionInput input,
			PrintStream printStream, boolean verbose) {
		
		RegressionWindow regressionWindow = getActiveWindow(apiClient, input, Collections.emptyList(), printStream);
		
		RateRegression result = calculateRateRegressions(apiClient, input, regressionWindow, printStream, verbose);
		
		return result;
	}

	public static RateRegression calculateRateRegressions(ApiClient apiClient, RegressionInput input,
			RegressionWindow regressionWindow, PrintStream printStream, boolean verbose) {

		if (printStream != null) {
			printStream.println("Begin regression analysis");
		}

		RateRegression.Builder builder = new RateRegression.Builder();

		if ((regressionWindow.activeTimespan == 0) && (regressionWindow.deploymentNotFound)) {

			if (printStream != null) {
				printStream.println(
						"No active timespan set and no deployment volume found in baseline - skipping analysis");
			}

			return builder.build();
		}

		builder.setActiveWindowStart(regressionWindow.activeWindowStart);
		DateTime baselineStart = regressionWindow.activeWindowStart.minusMinutes(input.baselineTimespan);

		if (printStream != null) {
			printStream.println("Regression Active window starts at: " + regressionWindow.activeWindowStart);
			printStream.println("Regression Baseline window starts at: " + baselineStart);
		}

		Collection<EventResult> events = getActiveEventVolume(apiClient, input, regressionWindow.activeWindowStart,
				printStream);

		if (events == null) {
			return builder.build();
		}

		Graph baselineGraph;
		Map<String, long[]> periodVolumes;
		Map<String, RegressionStats> regressionsStats;

		boolean hasRegressionDeltas = (input.regressionDelta > 0) || (input.criticalRegressionDelta > 0);

		if ((!hasRegressionDeltas) && (printStream != null)) {
			printStream.println("No regression deltas set for input. Regressions will not be calcualated.");
		}

		if (hasRegressionDeltas) {

			baselineGraph = getBaselineGraph(apiClient, input, baselineStart, regressionWindow.activeWindowStart,
					regressionWindow.activeTimespan, printStream);

			if (baselineGraph != null) {
				regressionsStats = processEventsGraph(baselineGraph);
				periodVolumes = getPeriodVolumes(regressionWindow.activeWindowStart, baselineGraph,
						regressionWindow.activeTimespan, input.baselineTimespan);
			} else {
				regressionsStats = null;
				periodVolumes = null;
			}
		} else {
			regressionsStats = null;
			periodVolumes = null;
			baselineGraph = null;
		}

		for (EventResult activeEvent : events) {

			if (activeEvent.error_location == null) {
				if (printStream != null) {
					printStream.println("Event has no location: " + activeEvent.summary);
				}
				continue;
			}

			RegressionState newState = processNewsIssueRegression(activeEvent, regressionWindow.activeWindowStart,
					input, builder, printStream, verbose);

			if ((newState == RegressionState.YES) 
			|| (newState == RegressionState.NO_DATA)) {
				continue;
			}

			if ((baselineGraph != null) && (regressionsStats != null) && (periodVolumes != null)) {
				processVolumeRegression(activeEvent, input, regressionsStats, periodVolumes,
						regressionWindow.activeTimespan, builder, printStream, verbose);
			} else {
				builder.addNonRegressions(activeEvent);
			}
		}

		RateRegression result = builder.build();

		if (verbose) {
			printNonRegressions(result, printStream);
		}

		return result;
	}

	private static void printNonRegressions(RateRegression rateRegression, PrintStream printStream) {
		if (printStream != null) {

			List<EventResult> nonRegressions = new ArrayList<EventResult>(rateRegression.getNonRegressions());
			nonRegressions.sort(new Comparator<EventResult>() {

				@Override
				public int compare(EventResult o1, EventResult o2) {
					return (int) o1.stats.hits - (int) o2.stats.hits;
				}
			});

			printStream.println("\nNon regressions:\n");

			for (EventResult event : nonRegressions) {
				printStream.println(event.stats.hits + " " + printEvent(event));
			}
		}
	}

	private static Map<String, RegressionStats> processEventsGraph(Graph graph) {
		// Maps from event id to a list of hits/invocations.
		// Uses long[] to optimize data size, we know how many points are there based on
		// the graph.
		Map<String, Pair<long[], long[]>> rawData = new HashMap<>();

		int pointsCount = graph.points.size();

		for (int i = 0; i < pointsCount; i++) {
			GraphPoint currentPoint = graph.points.get(i);

			if (CollectionUtil.safeIsEmpty(currentPoint.contributors)) {
				continue;
			}

			for (GraphPointContributor contributor : currentPoint.contributors) {
				if (contributor.stats == null) {
					continue;
				}

				Pair<long[], long[]> eventData = rawData.get(contributor.id);

				if (eventData == null) {
					eventData = Pair.of(new long[pointsCount], new long[pointsCount]);
					rawData.put(contributor.id, eventData);
				}

				eventData.getFirst()[i] = contributor.stats.hits;
				eventData.getSecond()[i] = contributor.stats.invocations;
			}
		}

		Map<String, RegressionStats> result = new HashMap<>(rawData.size());

		for (Map.Entry<String, Pair<long[], long[]>> entry : rawData.entrySet()) {
			result.put(entry.getKey(), buildRegressionStats(entry.getValue()));
		}

		return result;
	}

	private static RegressionStats buildRegressionStats(Pair<long[], long[]> eventStats) {
		long[] hitsArr = eventStats.getFirst();
		long[] invocationsArr = eventStats.getSecond();

		double[] rateArr = getRateArr(hitsArr, invocationsArr);

		long hitsSum = MathUtil.sum(hitsArr);
		long invocationsSum = MathUtil.sum(invocationsArr);
		double overallRate = ((invocationsSum == 0) ? 0.0 : ((double) hitsSum / (double) invocationsSum));

		double hitsAvg = MathUtil.avg(hitsArr);
		double invocationsAvg = MathUtil.avg(invocationsArr);
		double rateAvg = MathUtil.avg(rateArr);

		double hitsAvgStdDev = MathUtil.stdDev(hitsArr);
		double invocationsAvgStdDev = MathUtil.stdDev(invocationsArr);
		double rateAvgStdDev = MathUtil.stdDev(rateArr);

		return RegressionStats.of(hitsSum, invocationsSum, overallRate, hitsAvg, invocationsAvg, rateAvg, hitsAvgStdDev,
				invocationsAvgStdDev, rateAvgStdDev);
	}

	public static double[] getRateArr(long[] hits, long[] invocations) {
		double[] result = new double[hits.length];

		for (int i = 0; i < hits.length; i++) {
			long curHits = hits[i];
			long curInvocations = invocations[i];

			result[i] = ((curInvocations == 0) ? 0.0 : ((double) curHits / (double) curInvocations));
		}

		return result;
	}
}
