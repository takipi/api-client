package com.takipi.api.client.request.alert;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class Anomaly {
	private final List<AnomalyPeriod> anomalyPeriods;
	private final List<AnomalyContributor> anomalyContributors;

	private Anomaly() {
		this.anomalyPeriods = Lists.newArrayList();
		this.anomalyContributors = Lists.newArrayList();
	}

	public void addAnomalyPeriod(String id, long fromTimestamp, long toTimestamp) {
		this.anomalyPeriods.add(AnomalyPeriod.of(id, fromTimestamp, toTimestamp));
	}

	public boolean isEmpty() {
		return this.anomalyPeriods.isEmpty();
	}

	public int periodsCount() {
		return this.anomalyPeriods.size();
	}

	public List<AnomalyPeriod> getAnomalyPeriods() {
		return Collections.unmodifiableList(anomalyPeriods);
	}

	public void addContributor(int id, long value) {
		this.anomalyContributors.add(AnomalyContributor.of(id, value));
	}

	public int contributorsCount() {
		return this.anomalyContributors.size();
	}

	public List<AnomalyContributor> getAnomalyContributors() {
		return Collections.unmodifiableList(anomalyContributors);
	}

	public static Anomaly create() {
		return new Anomaly();
	}

	public static class AnomalyPeriod {
		public final String id;
		public final long fromTimestamp;
		public final long toTimestamp;

		private AnomalyPeriod(String id, long fromTimestamp, long toTimestamp) {
			this.id = id;
			this.fromTimestamp = fromTimestamp;
			this.toTimestamp = toTimestamp;
		}

		static AnomalyPeriod of(String id, long fromTimestamp, long toTimestamp) {
			return new AnomalyPeriod(id, fromTimestamp, toTimestamp);
		}
	}

	public static class AnomalyContributor {
		public final int id;
		public final long value;

		private AnomalyContributor(int id, long value) {
			this.id = id;
			this.value = value;
		}

		static AnomalyContributor of(int id, long value) {
			return new AnomalyContributor(id, value);
		}
	}

}
