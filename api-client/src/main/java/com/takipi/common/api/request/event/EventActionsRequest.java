package com.takipi.common.api.request.event;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.event.EventActionsResult;
import com.takipi.common.api.util.ValidationUtil;

public class EventActionsRequest extends ServiceRequest implements ApiGetRequest<EventActionsResult> {

	private final String eventId;

	EventActionsRequest(String serviceId, String eventId) {
		super(serviceId);
		this.eventId = eventId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/actions";
	}

	@Override
	public Class<EventActionsResult> resultClass() {
		return EventActionsResult.class;
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

		public EventActionsRequest build() {
			validate();

			return new EventActionsRequest(serviceId, eventId);
		}
	}
}
