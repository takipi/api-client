package com.takipi.common.api.request.event;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;
import com.takipi.common.api.util.ValidationUtil;

public class EventMarkResolvedRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String eventId;

	EventMarkResolvedRequest(String serviceId, String eventId) {
		super(serviceId);

		this.eventId = eventId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/resolve";
	}

	@Override
	public byte[] postData() {
		return null;
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
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

		public EventMarkResolvedRequest build() {
			validate();

			return new EventMarkResolvedRequest(serviceId, eventId);
		}
	}
}
