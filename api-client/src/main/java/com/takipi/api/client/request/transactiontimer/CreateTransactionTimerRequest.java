package com.takipi.api.client.request.transactiontimer;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class CreateTransactionTimerRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String className;
	private final String methodName;
	private final long threshold;

	CreateTransactionTimerRequest(String serviceId, String className, String methodName, long threshold) {
		super(serviceId);

		this.className = className;
		this.methodName = methodName;
		this.threshold = threshold;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/transaction-timers";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = ImmutableMap.of("class_name", JsonUtil.stringify(className), "method_name",
				JsonUtil.stringify(methodName), "threshold", Long.toString(threshold));

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
		private String className;
		private String methodName;
		private long threshold;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setClassName(String className) {
			this.className = className;

			return this;
		}

		public Builder setMethodName(String methodName) {
			this.methodName = methodName;

			return this;
		}

		public Builder setThreshold(long threshold) {
			this.threshold = threshold;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (Strings.isNullOrEmpty(className)) {
				throw new IllegalArgumentException("Missing class name");
			}

			if (Strings.isNullOrEmpty(methodName)) {
				throw new IllegalArgumentException("Missing method name");
			}

			if (threshold <= 0L) {
				throw new IllegalArgumentException("Threshold must be positive");
			}
		}

		public CreateTransactionTimerRequest build() {
			validate();

			return new CreateTransactionTimerRequest(serviceId, className, methodName, threshold);
		}
	}
}
