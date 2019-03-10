package com.takipi.api.client.request.process;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.process.JvmsResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class JvmsRequest extends ServiceRequest implements ApiGetRequest<JvmsResult> {
	private final boolean connected;

	JvmsRequest(String serviceId, boolean connected) {
		super(serviceId);

		this.connected = connected;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/overops-status/jvms";
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		String[] params = new String[1];

		params[0] = "connected=" + String.valueOf(connected);

		return params;
	}

	@Override
	public Class<JvmsResult> resultClass() {
		return JvmsResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private boolean connected;

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

		public JvmsRequest build() {
			validate();

			return new JvmsRequest(serviceId, connected);
		}
	}
}
