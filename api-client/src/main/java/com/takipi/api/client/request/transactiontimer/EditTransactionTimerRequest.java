package com.takipi.api.client.request.transactiontimer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class EditTransactionTimerRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final int timerId;
	private final long threshold;

	EditTransactionTimerRequest(String serviceId, int timerId, long threshold) {
		super(serviceId);

		this.timerId = timerId;
		this.threshold = threshold;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/transaction-timers/" + Integer.toString(timerId);
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = ImmutableMap.of("threshold", Long.toString(threshold));

		return JsonUtil.createSimpleJson(map, false).getBytes(ApiConstants.UTF8_ENCODING);
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
		private long threshold;

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

		public Builder setThreshold(long threshold) {
			this.threshold = threshold;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();
			
			if (timerId <= 0L) {
				throw new IllegalArgumentException("Timer id must be positive");
			}

			if (threshold <= 0L) {
				throw new IllegalArgumentException("Threshold must be positive");
			}
		}

		public EditTransactionTimerRequest build() {
			validate();

			return new EditTransactionTimerRequest(serviceId, timerId, threshold);
		}
	}
}
