package com.takipi.common.api.request;

import java.io.UnsupportedEncodingException;

public abstract class ProcessTagRequest extends ServiceRequest {
	private final boolean active;

	protected ProcessTagRequest(String serviceId, boolean active) {
		super(serviceId);

		this.active = active;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return new String[] { "active=" + String.valueOf(active) };
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		protected boolean active;

		protected Builder() {

		}

		public Builder setActive(boolean active) {
			this.active = active;

			return this;
		}
	}
}