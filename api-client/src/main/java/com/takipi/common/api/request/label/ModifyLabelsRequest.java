package com.takipi.common.api.request.label;

import java.io.UnsupportedEncodingException;

import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;

public abstract class ModifyLabelsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final boolean forceHistory;
	private final boolean handleSimilarEvents;

	protected ModifyLabelsRequest(String serviceId, boolean forceHistory, boolean handleSimilarEvents) {
		super(serviceId);

		this.forceHistory = forceHistory;
		this.handleSimilarEvents = handleSimilarEvents;
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		return null;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		String[] params = new String[2];

		params[0] = "force=" + Boolean.toString(forceHistory);
		params[0] = "handle_similar_events=" + Boolean.toString(handleSimilarEvents);

		return params;
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		protected boolean forceHistory;
		protected boolean handleSimilarEvents;

		protected Builder() {
			this.forceHistory = false;
			this.handleSimilarEvents = true;
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setForceHistory(boolean forceHistory) {
			this.forceHistory = forceHistory;

			return this;
		}

		public Builder setHandleSimilarEvents(boolean handleSimilarEvents) {
			this.handleSimilarEvents = handleSimilarEvents;

			return this;
		}
	}
}
