package com.takipi.api.client.request.transactiontimer;

import java.io.UnsupportedEncodingException;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;

public class ToggleTransactionTimerRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final int timerId;
	private final boolean enable;

	ToggleTransactionTimerRequest(String serviceId, int timerId, boolean enable) {
		super(serviceId);

		this.timerId = timerId;
		this.enable = enable;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/transaction-timers/" + Integer.toString(timerId) + (enable ? "/enable" : "/disable");
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
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
		private int timerId;
		private boolean enable;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setTimerId(int timerId) {
			this.timerId = timerId;

			return this;
		}

		public Builder setEnable(boolean enable) {
			this.enable = enable;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (timerId <= 0L) {
				throw new IllegalArgumentException("Timer id must be positive");
			}
		}

		public ToggleTransactionTimerRequest build() {
			validate();

			return new ToggleTransactionTimerRequest(serviceId, timerId, enable);
		}
	}
}
