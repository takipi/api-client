package com.takipi.api.client.request;

import java.util.Collection;

import com.takipi.common.util.ValidationUtil;

public abstract class EventTimeframeRequest extends TimeframeRequest {
	public final String eventId;

	protected EventTimeframeRequest(String serviceId, String eventId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId, from, to, servers, apps, deployments);

		this.eventId = eventId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends TimeframeRequest.Builder {
		protected String eventId;

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
	}
}
