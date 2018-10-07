
package com.takipi.common.api.request.event;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.event.EventResult;

public class EventForceSnapshotRequest extends ServiceRequest implements ApiPostRequest<EventResult> {
	private final String eventId;

	EventForceSnapshotRequest(String serviceId, String eventId) {
		super(serviceId);

		this.eventId = eventId;
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

	@Override
	public Class<EventResult> resultClass() {
		return EventResult.class;
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}
}
