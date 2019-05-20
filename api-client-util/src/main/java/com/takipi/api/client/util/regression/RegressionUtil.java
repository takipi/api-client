package com.takipi.api.client.util.regression;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.deployment.SummarizedDeployment;
import com.takipi.api.client.data.event.BaseStats;
import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.data.metrics.Graph.GraphPoint;
import com.takipi.api.client.data.metrics.Graph.GraphPointContributor;
import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.request.deployment.DeploymentsRequest;
import com.takipi.api.client.request.event.EventsVolumeRequest;
import com.takipi.api.client.result.deployment.DeploymentsResult;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;
import com.takipi.common.util.Pair;

public class RegressionUtil {

	public static final int POINT_FACTOR = 60;
	private static final int MAX_BASELINE_POINTS = 100;

	private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

	public static class RegressionWindow {
		public DateTime activeWindowStart;
		public int activeTimespan;
		public boolean deploymentFound;

		public Map<String, Pair<DateTime, DateTime>> deploymentsTimespan;
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
		Map<String, long[]> result = Maps.newHashMap();

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

		if ((activeEvent.stats == null) || (activeEvent.stats.invocations == 0) || (activeEvent.stats.hits == 0)) {

			if ((verbose) && (printStream != null)) {
				printStream.println("No stats " + ps(activeEvent.stats) + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return RegressionState.NO_DATA;
		}

		double activeEventRatio = ((double) activeEvent.stats.hits / (double) activeEvent.stats.invocations);

		double minVolumeThreshold = input.getEventMinThreshold(activeEvent);
		double minErrorRateThreshold = input.getEventMinErrorRateThreshold(activeEvent);

		boolean volumeExceeeded = (minVolumeThreshold > 0) && (activeEvent.stats.hits > minVolumeThreshold);
		boolean rateExceeded = (minErrorRateThreshold > 0) && (activeEventRatio > minErrorRateThreshold);

		if ((!volumeExceeeded) || (!rateExceeded)) {

			if ((verbose) && (printStream != null)) {

				printStream.println("Min threshold " + ps(activeEvent.stats) + printEvent(activeEvent) + "fails "
						+ "hits" + activeEvent.stats.hits + "ratio: " + activeEventRatio);
			}

			rateRegression.addNonRegressions(activeEvent);

			return RegressionState.NO_DATA;
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

				if (periodVolume > activeEvent.stats.hits) {
					largerVolumePriodIndex = index;
					largerVolumePeriod = periodVolume;
					break;
				}

				if (periodVolume > activeEvent.stats.hits / 2) {
					halfVolumePeriods++;
				}
			}
		}

		return SeasonlityResult.create(largerVolumePeriod, halfVolumePeriods, largerVolumePriodIndex);
	}

	private static RegressionState processVolumeRegression(EventResult activeEvent, RegressionInput input,
			Map<String, RegressionStats> regressionsStats, Map<String, long[]> periodVolumes, int activeTimespan,
			RateRegression.Builder rateRegression, PrintStream printStream, boolean verbose) {

		if (input.regressionDelta == 0) {
			return RegressionState.NO;
		}

		RegressionStats regressionStats = regressionsStats.get(activeEvent.id);

		if (regressionStats == null) {
			if ((verbose) && (printStream != null)) {
				printStream.println("No regression stats " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return RegressionState.NO_DATA;
		}

		double normalizedActiveVolume = (double) (activeEvent.stats.hits) / (double) (activeTimespan);
		double normalizedActiveInv = (double) (activeEvent.stats.invocations) / (double) (activeTimespan);

		double normalizedBaselineVolume = (double) (regressionStats.hits) / (double) (input.baselineTimespan);
		double normalizedBaselineInv = (double) (regressionStats.invocations) / (double) (input.baselineTimespan);

		double volRateDelta = ((normalizedActiveVolume) / (normalizedBaselineVolume)) - 1;
		double invRateDelta = ((normalizedActiveInv) / (normalizedBaselineInv)) - 1;

		double eventRegressionDelta = input.getEventRegressionDelta(activeEvent);
		double eventCriticalRegressionDelta = input.getEventCriticalRegressionDelta(activeEvent);

		boolean isCriticalRegression;
		boolean isRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > eventRegressionDelta;

		if (eventCriticalRegressionDelta > 0) {
			isCriticalRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > eventCriticalRegressionDelta;
		} else {
			isCriticalRegression = false;
		}

		if (!isRegression) {
			if ((verbose) && (printStream != null)) {
				printStream.println("Not regressed " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return RegressionState.NO;
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

			if (seasonlityResult.largerVolumePeriod >= 0) {
				if (printStream != null) {
					printStream.println("Period " + seasonlityResult.largerVolumePriodIndex + " = "
							+ seasonlityResult.largerVolumePeriod + " > active volume. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return RegressionState.NO;
			}

			if (seasonlityResult.halfVolumePeriods >= 2) {
				if (printStream != null) {
					printStream.println(seasonlityResult.halfVolumePeriods
							+ " periods > 50% active volume detected. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return RegressionState.NO;
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

		return RegressionState.YES;
	}

	private static void ApplyFilter(ViewTimeframeRequest.Builder builder, RegressionInput input, boolean applyDeps) {

		if (input.applictations != null) {
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

		String fromStr = from.toString(ISODateTimeFormat.dateTime().withZoneUTC());
		String toStr = to.toString(ISODateTimeFormat.dateTime().withZoneUTC());

		EventsVolumeRequest.Builder builder = EventsVolumeRequest.newBuilder().setServiceId(input.serviceId)
				.setViewId(input.viewId).setFrom(fromStr).setTo(toStr).setVolumeType(VolumeType.all);

		ApplyFilter(builder, input, true);

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

	public static DeploymentsTimespan getDeploymentsTimespan(ApiClient apiClient, String serviceId,
			Collection<String> deployments) {

		return getDeploymentsTimespan(apiClient, serviceId, deployments, null);
	}

	private static DeploymentsTimespan getDeploymentsTimespan(ApiClient apiClient, String serviceId,
			Collection<String> deployments, Collection<SummarizedDeployment> deploymentsSummary) {
		DeploymentsTimespan deploymentsTimespan = getDeploymentsTimespan(apiClient, serviceId, deployments,
				deploymentsSummary, true);

		if (deploymentsTimespan != null) {
			return deploymentsTimespan;
		}

		return getDeploymentsTimespan(apiClient, serviceId, deployments, deploymentsSummary, false);
	}

	private static DeploymentsTimespan getDeploymentsTimespan(ApiClient apiClient, String serviceId,
			Collection<String> deployments, Collection<SummarizedDeployment> deploymentsSummary, boolean active) {

		Collection<SummarizedDeployment> deploymentsData = deploymentsSummary;

		if (deploymentsData == null) {
			DeploymentsRequest request = DeploymentsRequest.newBuilder().setServiceId(serviceId).setActive(active)
					.build();

			Response<DeploymentsResult> response = apiClient.get(request);

			if ((response.isBadResponse()) || (response.data == null)) {
				throw new IllegalStateException(
						"Could not acquire deployments for service " + serviceId + " . Error " + response.responseCode);
			}

			if (response.data.deployments == null) {
				return null;
			}

			deploymentsData = response.data.deployments;
		}

		Map<String, Pair<DateTime, DateTime>> deploymentLifetime = Maps.newHashMap();

		Map<String, SummarizedDeployment> summarizedDeploymentByName = Maps.newHashMap();

		for (SummarizedDeployment summaryDeployment : deploymentsData) {
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

			deploymentLifetime.put(summarizedDeployment.name, Pair.of(start, end));
		}

		DeploymentsTimespan deploymentsTimespan = new DeploymentsTimespan(deploymentLifetime,
				Pair.of(minDeploymentStart, maxDeploymentEnd));

		return deploymentsTimespan;
	}

	public static RegressionWindow getActiveWindow(ApiClient apiClient, RegressionInput input,
			PrintStream printStream) {
		return getActiveWindow(apiClient, input, printStream, null);
	}

	public static RegressionWindow getActiveWindow(ApiClient apiClient, RegressionInput input, PrintStream printStream,
			Collection<SummarizedDeployment> deploymentsSummary) {

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

		DeploymentsTimespan deploymentsTimespan = getDeploymentsTimespan(apiClient, input.serviceId, input.deployments,
				deploymentsSummary);

		if (deploymentsTimespan == null) {
			printStream.println("Deployments timespan is null for serviceId: " + input.serviceId + "deployments: "
					+ Arrays.toString(input.deployments.toArray()));

			result.activeWindowStart = now.minusMinutes(input.activeTimespan);

			return result;
		}

		Pair<DateTime, DateTime> depTimespan = deploymentsTimespan.getActiveWindow();

		result.activeWindowStart = depTimespan.getFirst();

		if (result.activeWindowStart == null) {
			if (printStream != null) {
				printStream.println(
						"Could not acquire start time for deployments " + Arrays.toString(input.deployments.toArray()));
			}

			return result;
		}

		DateTime activeWindowEnd;

		if (depTimespan.getSecond() != null) {
			activeWindowEnd = depTimespan.getSecond();
		} else {
			activeWindowEnd = now;
		}

		result.activeTimespan = (int) TimeUnit.MILLISECONDS
				.toMinutes(activeWindowEnd.minus(result.activeWindowStart.getMillis()).getMillis());

		if (result.activeTimespan <= 0) {
			result.activeTimespan = (int) (TimeUnit.DAYS.toMinutes(1));
			result.activeWindowStart = now.minusDays(1);
		}

		result.deploymentFound = true;
		result.deploymentsTimespan = deploymentsTimespan.getDeploymentsLifetimeMap();

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

		Collection<EventResult> result;

		if (input.events != null) {
			result = input.events;
		} else {

			// add one minute in case the date range is not a full minute which causes the
			// query to fail
			EventsResult activeEventVolume = getEventsVolume(apiClient, input, activeWindowStart, DateTime.now());

			if (!validateVolume(apiClient, activeEventVolume, input, printStream)) {
				return null;
			}

			result = activeEventVolume.events;
		}

		return result;
	}

	public static RateRegression calculateRateRegressions(ApiClient apiClient, RegressionInput input,
			PrintStream printStream, boolean verbose) {

		if (printStream != null) {
			printStream.println("Begin regression analysis");
		}

		RateRegression.Builder builder = new RateRegression.Builder();

		RegressionWindow regressionWindow = getActiveWindow(apiClient, input, printStream);

		if ((regressionWindow.activeTimespan == 0) && (!regressionWindow.deploymentFound)) {

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

			RegressionState newIssueState = processNewsIssueRegression(activeEvent, regressionWindow.activeWindowStart,
					input, builder, printStream, verbose);

			if ((newIssueState.equals(RegressionState.YES)) || (newIssueState.equals(RegressionState.NO_DATA))) {
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
		Map<String, Pair<long[], long[]>> rawData = Maps.newHashMap();

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

		Map<String, RegressionStats> result = Maps.newHashMapWithExpectedSize(rawData.size());

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
