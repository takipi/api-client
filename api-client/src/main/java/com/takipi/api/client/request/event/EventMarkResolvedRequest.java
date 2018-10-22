package com.takipi.api.client.request.event;

import com.takipi.api.client.request.label.ModifyLabelsRequest;
import com.takipi.common.util.ValidationUtil;

public class EventMarkResolvedRequest extends ModifyLabelsRequest {
	private final String eventId;

	EventMarkResolvedRequest(String serviceId, String eventId, boolean forceHistory, boolean handleSimilarEvents) {
		super(serviceId, forceHistory, handleSimilarEvents);

		this.eventId = eventId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/resolve";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ModifyLabelsRequest.Builder {
		private String eventId;

		Builder() {

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

		public Builder setEventId(String eventId) {
			this.eventId = eventId;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalEventId(eventId)) {
				throw new IllegalArgumentException("Illegal event id - " + eventId);
			}
		}

		public EventMarkResolvedRequest build() {
			validate();

			return new EventMarkResolvedRequest(serviceId, eventId, forceHistory, handleSimilarEvents);
		}
	}
}
