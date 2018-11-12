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
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.event.Stats;
import com.takipi.api.client.data.metrics.Graph;
import com.takipi.api.client.data.metrics.Graph.GraphPoint;
import com.takipi.api.client.data.metrics.Graph.GraphPointContributor;
import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.request.event.EventsVolumeRequest;
import com.takipi.api.client.request.metrics.GraphRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.result.event.EventsVolumeResult;
import com.takipi.api.client.result.metrics.GraphResult;
import com.takipi.api.client.util.validation.ValidationUtil.GraphType;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;
import com.takipi.common.util.Pair;

public class RegressionUtil {

	private static final DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();
	
	enum Regression {
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
			DateTime firstSeen = ISODateTimeFormat.dateTimeParser().parseDateTime(graphPoint.time);
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

	private static String ps(Stats stats) {
		if (stats == null) {
			return "(null)";
		}

		return "(" + stats.hits + "/" + stats.invocations + ")";
	}

	private static Regression processNewsIssueRegression(EventResult activeEvent, DateTime activeFrom,
			RegressionInput input, RateRegression.Builder rateRegression, PrintStream printStream, boolean verbose) {

		DateTime firstSeen = ISODateTimeFormat.dateTimeParser().parseDateTime(activeEvent.first_seen);

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

				return Regression.YES;
			}
		}

		if ((activeEvent.stats == null) || (activeEvent.stats.invocations == 0) || (activeEvent.stats.hits == 0)) {

			if ((verbose) && (printStream != null)) {
				printStream.println("No stats " + ps(activeEvent.stats) + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return Regression.NO_DATA;
		}

		double activeEventRatio = ((double) activeEvent.stats.hits / (double) activeEvent.stats.invocations);

		if ((activeEventRatio < input.minErrorRateThreshold) || (activeEvent.stats.hits < input.minVolumeThreshold)) {

			if ((verbose) && (printStream != null)) {

				printStream.println("Min threshold " + ps(activeEvent.stats) + printEvent(activeEvent) + "fails "
						+ "hits" + activeEvent.stats.hits + "ratio: " + activeEventRatio);
			}

			rateRegression.addNonRegressions(activeEvent);

			return Regression.NO_DATA;
		}

		if (isNew) {
			rateRegression.addExceddedNewEvent(activeEvent.id, activeEvent);

			if (printStream != null) {
				printStream.println(printEvent(activeEvent) + " is new with ER: " + activeEventRatio + " hits: "
						+ activeEvent.stats.hits);
			}

			return Regression.YES;
		}

		return Regression.NO;
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

	private static Regression processVolumeRegression(EventResult activeEvent, RegressionInput input,
			Map<String, RegressionStats> regressionsStats, Map<String, long[]> periodVolumes, int activeTimespan,
			RateRegression.Builder rateRegression, PrintStream printStream, boolean verbose) {

		if (input.regressionDelta == 0) {
			return Regression.NO;
		}

		RegressionStats regressionStats = regressionsStats.get(activeEvent.id);

		if (regressionStats == null) {
			if ((verbose) && (printStream != null)) {
				printStream.println("No regression stats " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return Regression.NO_DATA;
		}
		
		double normalizedActiveVolume = (double) (activeEvent.stats.hits) / (double) (activeTimespan);
		double normalizedActiveInv = (double) (activeEvent.stats.invocations) / (double) (activeTimespan);

		double normalizedBaselineVolume = (double) (regressionStats.hits) / (double) (input.baselineTimespan);
		double normalizedBaselineInv = (double) (regressionStats.invocations) / (double) (input.baselineTimespan);

		double volRateDelta = ((double) (normalizedActiveVolume) / (double) (normalizedBaselineVolume)) - 1;
		double invRateDelta = ((double) (normalizedActiveInv) / (double) (normalizedBaselineInv)) - 1;

		boolean isRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > input.regressionDelta;
		boolean isCriticalRegression = volRateDelta - Math.max(invRateDelta * 2, 0) > input.criticalRegressionDelta;

		if (!isRegression) {
			if ((verbose) && (printStream != null)) {
				printStream.println("Not regressed " + printEvent(activeEvent));
			}

			rateRegression.addNonRegressions(activeEvent);

			return Regression.NO;
		}

		if (printStream != null) {
			printStream.println(printEvent(activeEvent) + " regression\nRelHit: " + f(normalizedBaselineVolume) + " -> "
					+ f(normalizedActiveVolume) + "\nRelInv: " + f(normalizedBaselineInv) + " -> "
					+ f(normalizedActiveInv) + "\nVolDel: " + f(volRateDelta) + ", InvDel " + f(invRateDelta)
					+ "\nhits: " + f(regressionStats.hits) + " -> " + f(activeEvent.stats.hits) + "\ninv: "
					+ f(regressionStats.invocations) + " -> " + f(activeEvent.stats.invocations));
		}

		SeasonlityResult seasonlityResult = null;

		if (input.applySeasonality) {

			seasonlityResult = calculateSeasonality(activeEvent, periodVolumes);

			if (seasonlityResult.largerVolumePeriod >= 0) {
				if (printStream != null) {
					printStream.println("Period " + seasonlityResult.largerVolumePriodIndex + " = "
							+ seasonlityResult.largerVolumePeriod + " > active volume. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return Regression.NO;
			}

			if (seasonlityResult.halfVolumePeriods >= 2) {
				if (printStream != null) {
					printStream.println(seasonlityResult.halfVolumePeriods
							+ " periods > 50% active volume detected. Aborting regression\n");
				}

				rateRegression.addNonRegressions(activeEvent);

				return Regression.NO;
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

		return Regression.YES;
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

	public static EventsVolumeResult getEventsVolume(ApiClient apiClient, RegressionInput input, DateTime from,
			DateTime to) {

		String fromStr = from.toString(ISODateTimeFormat.dateTime().withZoneUTC());
		String toStr = to.toString(ISODateTimeFormat.dateTime().withZoneUTC());

		EventsVolumeRequest.Builder builder = EventsVolumeRequest.newBuilder().setServiceId(input.serviceId)
				.setViewId(input.viewId).setFrom(fromStr).setTo(toStr).setVolumeType(VolumeType.all);

		ApplyFilter(builder, input, true);

		Response<EventsVolumeResult> response = apiClient.get(builder.build());

		if (response.isBadResponse()) {
			throw new IllegalStateException("Error querying volume data with code " + response.responseCode);
		}

		EventsVolumeResult result = response.data;

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

	private static boolean validateVolume(ApiClient apiClient, EventsVolumeResult eventResult, RegressionInput input,
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
	
	private static DateTime getDeploymentStartTime(ApiClient apiClient, String serviceId, 
		String viewId, DateTime from, DateTime to, int pointsCount, Collection<String> deployments) {

		GraphRequest.Builder builder = GraphRequest.newBuilder().setServiceId(serviceId).setViewId(viewId)
				.setGraphType(GraphType.view).setFrom(from.toString(fmt)).setTo(to.toString(fmt))
				.setVolumeType(VolumeType.all).setWantedPointCount(pointsCount);

		for (String dep : deployments) {
			builder.addDeployment(dep);
		}
		
		Response<GraphResult> graphResponse = apiClient.get(builder.build());

		if (graphResponse.isBadResponse()) {
			return null;
		}

		GraphResult graphResult = graphResponse.data;

		if (graphResult == null) {
			return null;
		}
		
		if (CollectionUtil.safeIsEmpty(graphResult.graphs)) {
			return null;
		}
	
		Graph graph = graphResult.graphs.get(0);
		
		if (CollectionUtil.safeIsEmpty(graph.points)) {
			return null;
		}
	
		for (GraphPoint gp : graph.points) {
			if ((gp.stats.hits != 0) || (gp.stats.invocations != 0)) {
				return ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime(gp.time);
			}
		}

		return null;
	}
	
	public static Pair<DateTime, Integer> getActiveWindow(ApiClient apiClient, RegressionInput input,
			PrintStream printStream) {

		int activeTimespan;
		DateTime activeWindowStart = null;

		if (input.activeWindowStart != null) {
			activeWindowStart = input.activeWindowStart;
			activeTimespan = input.activeTimespan;
		} else {

			DateTime now = DateTime.now();

			if (!CollectionUtil.safeIsEmpty(input.deployments)) {

				DateTime from = now.minusMinutes(input.baselineTimespan + input.activeTimespan);
				int pointsWanted = Days.daysBetween(from, now).getDays() * 4;
				
				activeWindowStart = getDeploymentStartTime(apiClient, input.serviceId, input.viewId,
						from, now, pointsWanted, input.deployments);

				if ((activeWindowStart == null) && (printStream != null)) {
					printStream.println("Could not acquire start time for deployment "
							+ Arrays.toString(input.deployments.toArray()));
				}
			}

			if (activeWindowStart != null) {
				activeTimespan = (int) TimeUnit.MILLISECONDS
						.toMinutes(now.minus(activeWindowStart.getMillis()).getMillis());
			} else {
				activeWindowStart = now.minusMinutes(input.activeTimespan);
				activeTimespan = input.activeTimespan;
			}
		}

		return Pair.of(activeWindowStart, Integer.valueOf(activeTimespan));
	}

	private static Graph getBaselineGraph(ApiClient apiClient, RegressionInput input, DateTime baselineStart,
			DateTime activeWindowStart, int activeTimespan, PrintStream printStream) {

		Graph result;

		if (input.baselineGraph != null) {
			result = input.baselineGraph;
		} else {
			int pointsWanted = input.baselineTimespan / activeTimespan * 2;

			if (pointsWanted > 0) {
				GraphResult graphResult = ViewUtil.getEventsGraphResult(apiClient, input.serviceId, input.viewId,
						pointsWanted, VolumeType.all, baselineStart, activeWindowStart);

				result = validateGraph(apiClient, graphResult, input, printStream);
			} else {
				result = null;
			}
		}

		return result;
	}

	private static Collection<EventResult> getActiveEventVolume(ApiClient apiClient, RegressionInput input,
			DateTime activeWindowStart, PrintStream printStream) {

		Collection<EventResult> result;

		if (input.events != null) {
			result = input.events;
		} else {

			EventsVolumeResult activeEventVolume = getEventsVolume(apiClient, input, activeWindowStart, DateTime.now());

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
			printStream.println(input + "\n");
		}

		RateRegression.Builder builder = new RateRegression.Builder();

		Pair<DateTime, Integer> activeWindow = getActiveWindow(apiClient, input, printStream);

		int activeTimespan = activeWindow.getSecond().intValue();
		DateTime activeWindowStart = activeWindow.getFirst();

		builder.setActiveWndowStart(activeWindowStart);
		DateTime baselineStart = activeWindowStart.minusMinutes(input.baselineTimespan);

		if (printStream != null) {
			printStream.println("Regression Active window starts at: " + activeWindowStart);
			printStream.println("Regression Baseline window starts at: " + baselineStart);
		}

		Collection<EventResult> events = getActiveEventVolume(apiClient, input, activeWindowStart, printStream);

		if (events == null) {
			return builder.build();
		}

		Graph baselineGraph = getBaselineGraph(apiClient, input, baselineStart, activeWindowStart, activeTimespan,
				printStream);

		Map<String, long[]> periodVolumes;
		Map<String, RegressionStats> regressionsStats;

		if (baselineGraph != null) {
			regressionsStats = processEventsGraph(baselineGraph);
			periodVolumes = getPeriodVolumes(activeWindowStart, baselineGraph, activeTimespan, input.baselineTimespan);
		} else {
			regressionsStats = null;
			periodVolumes = null;
		}

		for (EventResult activeEvent : events) {

			Regression isNewIssue = processNewsIssueRegression(activeEvent, activeWindowStart, input, builder,
					printStream, verbose);

			if ((isNewIssue.equals(Regression.YES)) || (isNewIssue.equals(Regression.NO_DATA))) {
				continue;
			}

			if (baselineGraph != null) {
				processVolumeRegression(activeEvent, input, regressionsStats, periodVolumes, activeTimespan, builder,
						printStream, verbose);
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
