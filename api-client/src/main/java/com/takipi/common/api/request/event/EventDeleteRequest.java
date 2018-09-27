package com.takipi.common.api.request.event;

import com.takipi.common.api.request.label.ModifyLabelsRequest;
import com.takipi.common.api.util.ValidationUtil;

public class EventDeleteRequest extends ModifyLabelsRequest {
	private final String eventId;

	EventDeleteRequest(String serviceId, String eventId, boolean forceHistory, boolean handleSimilarEvents) {
		super(serviceId, forceHistory, handleSimilarEvents);

		this.eventId = eventId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/delete";
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

		public EventDeleteRequest build() {
			validate();

			return new EventDeleteRequest(serviceId, eventId, forceHistory, handleSimilarEvents);
		}
	}
}
