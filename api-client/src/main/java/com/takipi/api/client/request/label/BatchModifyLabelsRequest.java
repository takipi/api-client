package com.takipi.api.client.request.label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;

public class BatchModifyLabelsRequest extends ModifyLabelsRequest {
	private final Collection<Modification> modifications;

	BatchModifyLabelsRequest(String serviceId, Collection<Modification> modifications, boolean forceHistory,
			boolean handleSimilarEvents) {
		super(serviceId, forceHistory, handleSimilarEvents);

		this.modifications = modifications;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/labels";
	}

	@Override
	public String postData() {
		Collection<String> itemJsons = new ArrayList<>();

		for (Modification modification : modifications) {
			String itemJson = JsonUtil
					.createSimpleJson(CollectionUtil.mapOf("event_id", JsonUtil.stringify(modification.eventId), "add",
							JsonUtil.createSimpleJson(modification.addLabels, true), "remove",
							JsonUtil.createSimpleJson(modification.removeLabels, true)));

			itemJsons.add(itemJson);
		}

		return JsonUtil.createSimpleJson(CollectionUtil.mapOf("items", JsonUtil.createSimpleJson(itemJsons, false)));
	}

	static class Modification {
		final String eventId;
		final Collection<String> addLabels;
		final Collection<String> removeLabels;

		private Modification(String eventId, Collection<String> addLabels, Collection<String> removeLabels) {
			this.eventId = eventId;
			this.addLabels = addLabels;
			this.removeLabels = removeLabels;
		}

		static Modification of(String eventId, Collection<String> addLabels, Collection<String> removeLabels) {
			return new Modification(eventId, addLabels, removeLabels);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ModifyLabelsRequest.Builder {
		private final Map<String, Modification> modifications;

		Builder() {
			this.modifications = new HashMap<>();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setForceHistory(boolean forceHistory) {
			super.setForceHistory(forceHistory);

			return this;
		}

		@Override
		public Builder setHandleSimilarEvents(boolean handleSimilarEvents) {
			super.setHandleSimilarEvents(handleSimilarEvents);

			return this;
		}

		public Builder addLabelModifications(String eventId, Collection<String> addLabels,
				Collection<String> removeLabels) {
			this.modifications.put(eventId, Modification.of(eventId, addLabels, removeLabels));

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (modifications.isEmpty()) {
				throw new IllegalArgumentException("No modifications provided");
			}

			for (Modification modification : modifications.values()) {
				if (!ValidationUtil.isLegalEventId(modification.eventId)) {
					throw new IllegalArgumentException("Illegal event id - " + modification.eventId);
				}

				if ((modification.addLabels.size() + modification.removeLabels.size()) == 0) {
					throw new IllegalArgumentException("Must modify labels of " + modification.eventId);
				}
			}
		}

		public BatchModifyLabelsRequest build() {
			validate();

			return new BatchModifyLabelsRequest(serviceId, modifications.values(), forceHistory, handleSimilarEvents);
		}
	}
}
