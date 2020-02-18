package com.takipi.api.client.request;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;

import com.takipi.api.client.util.validation.ValidationUtil;

public abstract class EventsTimeframeRequest extends TimeframeRequest {
	public final Collection<String> eventIds;

	protected EventsTimeframeRequest(String serviceId, Collection<String> eventIds, String from, String to,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments) {
		super(serviceId, from, to, servers, apps, deployments);

		this.eventIds = eventIds;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	protected int paramsCount() {
		return super.paramsCount() + eventIds.size();
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		for (String eventId : eventIds) {
			params[index++] = "eventIds=" + encode(eventId);
		}

		return index;
	}

	public static class Builder extends TimeframeRequest.Builder {
		protected Collection<String> eventIds;

		protected Builder() {
			this.eventIds = new HashSet<>();
		}

		public Builder addEventId(String eventId) {
			this.eventIds.add(eventId);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (eventIds.isEmpty()) {
				throw new IllegalArgumentException("No event ids provided");
			}

			for (String eventId : eventIds) {
				if (!ValidationUtil.isLegalEventId(eventId)) {
					throw new IllegalArgumentException("Illegal event id - " + eventId);
				}
			}
		}
	}
}
