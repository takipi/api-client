package com.takipi.api.client.request.alert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.takipi.api.client.data.alert.Anomaly;
import com.takipi.api.client.data.alert.Anomaly.AnomalyContributor;
import com.takipi.api.client.data.alert.Anomaly.AnomalyPeriod;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class AnomalyAlertRequest extends AlertRequest {
	private final String desc;

	private final String from;
	private final String to;
	private final String prettyTimeframe;

	private final Anomaly anomaly;

	protected AnomalyAlertRequest(String serviceId, String viewId, String desc, String from, String to,
			String prettyTimeframe, Anomaly anomaly) {
		super(serviceId, viewId);

		this.desc = desc;

		this.from = from;
		this.to = to;
		this.prettyTimeframe = prettyTimeframe;

		this.anomaly = anomaly;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/anomaly";
	}

	@Override
	public String postData() {
		Map<String, String> map = new HashMap<>();

		map.put("desc", JsonUtil.stringify(desc));
		map.put("timeframe", timeframeJson());
		map.put("anomaly", anomalyJson());

		return JsonUtil.createSimpleJson(map, false);
	}

	private String timeframeJson() {
		return JsonUtil.createSimpleJson(
				CollectionUtil.mapOf("from", from, "to", to, "name", StringUtil.nullToEmpty(prettyTimeframe)), true);
	}

	private String anomalyJson() {
		Collection<String> periodsJsons = new ArrayList<>(anomaly.periodsCount());

		for (AnomalyPeriod period : anomaly.getAnomalyPeriods()) {
			periodsJsons.add(JsonUtil.createSimpleJson(CollectionUtil.mapOf("id", period.id, "from",
					new DateTime(period.fromTimestamp).toString(), "to", new DateTime(period.toTimestamp).toString()),
					true));
		}

		Collection<String> contributorsJsons = new ArrayList<>(anomaly.contributorsCount());

		for (AnomalyContributor contributor : anomaly.getAnomalyContributors()) {
			contributorsJsons.add(JsonUtil.createSimpleJson(
					CollectionUtil.mapOf("id", String.valueOf(contributor.id), "value", String.valueOf(contributor.value))));
		}

		return JsonUtil.createSimpleJson(CollectionUtil.mapOf("periods", JsonUtil.createSimpleJson(periodsJsons),
				"contributors", JsonUtil.createSimpleJson(contributorsJsons)));
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends AlertRequest.Builder {
		private String desc;
		private String from;
		private String to;
		private String prettyTimeframe;
		private Anomaly anomaly;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setViewId(String viewId) {
			super.setViewId(viewId);

			return this;
		}

		public Builder setDesc(String desc) {
			this.desc = desc;

			return this;
		}

		public Builder setFrom(String from) {
			this.from = from;

			return this;
		}

		public Builder setTo(String to) {
			this.to = to;

			return this;
		}

		public Builder setPrettyTimeframe(String prettyTimeframe) {
			this.prettyTimeframe = prettyTimeframe;

			return this;
		}

		public Builder setAnomaly(Anomaly anomaly) {
			this.anomaly = anomaly;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (StringUtil.isNullOrEmpty(desc)) {
				throw new IllegalArgumentException("Empty description");
			}

			DateTime fromDate;

			try {
				fromDate = DateTime.parse(from);
			} catch (Exception e) {
				throw new IllegalArgumentException("Illegal from date - " + from, e);
			}

			DateTime toDate;

			try {
				toDate = DateTime.parse(to);
			} catch (Exception e) {
				throw new IllegalArgumentException("Illegal to date - " + from, e);
			}

			if (!fromDate.isBefore(toDate)) {
				throw new IllegalArgumentException("From date " + from + " must be before to date " + to);
			}

			if (anomaly == null) {
				throw new IllegalArgumentException("No anomaly set");
			}
		}

		public AnomalyAlertRequest build() {
			validate();

			return new AnomalyAlertRequest(serviceId, viewId, desc, from, to, prettyTimeframe, anomaly);
		}
	}
}
