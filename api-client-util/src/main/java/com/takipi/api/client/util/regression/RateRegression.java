package com.takipi.api.client.util.regression;

import java.util.Collections;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.takipi.api.client.result.event.EventResult;

public class RateRegression {
	private final Map<String, EventResult> allNewEvents;

	private final Map<String, RegressionResult> allRegressions;
	private final Map<String, RegressionResult> criticalRegressions;

	private final Map<String, EventResult> exceededNewEvents;
	private final Map<String, EventResult> criticalNewEvents;

	private final DateTime activeWndowStart;

	RateRegression(Map<String, EventResult> allNewEvents, Map<String, RegressionResult> allRegressions,
			Map<String, RegressionResult> criticalRegressions, Map<String, EventResult> exceededNewEvents,
			Map<String, EventResult> criticalNewEvents, DateTime activeWndowStart) {

		this.activeWndowStart = activeWndowStart;
		this.allRegressions = Collections.unmodifiableMap(allRegressions);
		this.criticalNewEvents = Collections.unmodifiableMap(criticalNewEvents);
		this.exceededNewEvents = Collections.unmodifiableMap(exceededNewEvents);
		this.allNewEvents = Collections.unmodifiableMap(allNewEvents);
		this.criticalRegressions = Collections.unmodifiableMap(criticalRegressions);
	}

	public DateTime getActiveWndowStart() {
		return activeWndowStart;
	}

	public Map<String, EventResult> getAllNewEvents() {
		return allNewEvents;
	}

	public Map<String, RegressionResult> getAllRegressions() {
		return allRegressions;
	}

	public Map<String, RegressionResult> getCriticalRegressions() {
		return criticalRegressions;
	}

	public Map<String, EventResult> getExceededNewEvents() {
		return exceededNewEvents;
	}

	public Map<String, EventResult> getCriticalNewEvents() {
		return criticalNewEvents;
	}

	public static class Builder {
		private final Map<String, EventResult> allNewEvents;

		private final Map<String, RegressionResult> allRegressions;
		private final Map<String, RegressionResult> criticalRegressions;

		private final Map<String, EventResult> exceededNewEvents;
		private final Map<String, EventResult> criticalNewEvents;

		private DateTime activeWndowStart;

		Builder() {
			allRegressions = Maps.newHashMap();
			criticalNewEvents = Maps.newHashMap();
			exceededNewEvents = Maps.newHashMap();
			allNewEvents = Maps.newHashMap();
			criticalRegressions = Maps.newHashMap();
		}

		public void setActiveWndowStart(DateTime activeWndowStart) {
			this.activeWndowStart = activeWndowStart;
		}

		public void addNewEvent(String id, EventResult event) {
			allNewEvents.put(id, event);
		}

		public void addExceddedNewEvent(String id, EventResult event) {
			exceededNewEvents.put(id, event);
		}

		public void addCriticalNewEvent(String id, EventResult event) {
			criticalNewEvents.put(id, event);
		}

		public void addRegression(String id, EventResult event, long baselineHits, long baselineInvocations) {
			allRegressions.put(id, RegressionResult.of(event, baselineHits, baselineInvocations));
		}

		public void addCriticalRegression(String id, EventResult event, long baselineHits, long baselineInvocations) {
			criticalRegressions.put(id, RegressionResult.of(event, baselineHits, baselineInvocations));
		}

		public RateRegression build() {
			return new RateRegression(allNewEvents, allRegressions, criticalRegressions, exceededNewEvents,
					criticalNewEvents, activeWndowStart);
		}
	}
}
