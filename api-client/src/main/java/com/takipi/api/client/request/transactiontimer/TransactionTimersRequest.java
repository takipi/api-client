package com.takipi.api.client.request.transactiontimer;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.transactiontimer.TransactionTimersResult;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class TransactionTimersRequest extends ServiceRequest implements ApiGetRequest<TransactionTimersResult> {
	TransactionTimersRequest(String serviceId) {
		super(serviceId);
	}

	@Override
	public Class<TransactionTimersResult> resultClass() {
		return TransactionTimersResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/transaction-timers";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public TransactionTimersRequest build() {
			validate();

			return new TransactionTimersRequest(serviceId);
		}
	}
}
