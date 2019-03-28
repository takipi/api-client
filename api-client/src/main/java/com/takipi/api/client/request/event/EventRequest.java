package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventRequest extends ServiceRequest implements ApiGetRequest<EventResult> {
	public final String eventId;
	public final boolean includeStacktrace;

	EventRequest(String serviceId, String eventId, boolean includeStacktrace) {
		super(serviceId);

		this.eventId = eventId;
		this.includeStacktrace = includeStacktrace;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		String[] params = new String[1];

		params[0] = "stacktrace=" + Boolean.toString(includeStacktrace);

		return params;
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
		private boolean includeStacktrace;

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

		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			this.includeStacktrace = includeStacktrace;

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

			return new EventRequest(serviceId, eventId, includeStacktrace);
		}
	}
}
