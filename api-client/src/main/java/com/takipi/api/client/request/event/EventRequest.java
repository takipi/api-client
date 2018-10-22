package com.takipi.api.client.request.event;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventRequest extends ServiceRequest implements ApiGetRequest<EventResult> {
	private final String eventId;

	EventRequest(String serviceId, String eventId) {
		super(serviceId);

		this.eventId = eventId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId;
	}

	@Override
	public Class<EventResult> resultClass() {
		return EventResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String eventId;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

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

		public EventRequest build() {
			validate();

			return new EventRequest(serviceId, eventId);
		}
	}
}
