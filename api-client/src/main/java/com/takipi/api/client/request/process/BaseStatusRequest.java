package com.takipi.api.client.request.process;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;

public abstract class BaseStatusRequest extends ServiceRequest {
	public final boolean connected;

	private final String type;

	BaseStatusRequest(String serviceId, boolean connected, String type) {
		super(serviceId);

		this.connected = connected;
		this.type = type;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/overops-status/" + type;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		String[] params = new String[1];

		params[0] = "connected=" + String.valueOf(connected);

		return params;
	}

	public static class Builder extends ServiceRequest.Builder {
		protected boolean connected;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setConnected(boolean connected) {
			this.connected = connected;

			return this;
		}
	}
}
