package com.takipi.api.client.request.transactiontimer;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiDeleteRequest;

public class DeleteTransactionTimerRequest extends ServiceRequest implements ApiDeleteRequest<EmptyResult> {
	private final int timerId;

	DeleteTransactionTimerRequest(String serviceId, int timerId) {
		super(serviceId);

		this.timerId = timerId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/transaction-timers/" + Integer.toString(timerId);
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

		@Override
		protected void validate() {
			super.validate();

			if (timerId <= 0) {
				throw new IllegalArgumentException("Timer id must be positive");
			}
		}

		public DeleteTransactionTimerRequest build() {
			validate();

			return new DeleteTransactionTimerRequest(serviceId, timerId);
		}
	}
}
