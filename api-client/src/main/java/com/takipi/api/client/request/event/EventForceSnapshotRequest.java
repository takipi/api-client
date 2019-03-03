package com.takipi.api.client.request.event;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;

public class EventForceSnapshotRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String eventId;

	EventForceSnapshotRequest(String serviceId, String eventId) {
		super(serviceId);

		this.eventId = eventId;
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	@Override
	public String postData() {
		return null;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/force-snapshot";
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

		public EventForceSnapshotRequest build() {
			validate();

			return new EventForceSnapshotRequest(serviceId, eventId);
		}
	}
}
