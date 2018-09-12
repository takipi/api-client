package com.takipi.common.api.request.timer;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;
import com.takipi.common.api.util.JsonUtil;

public class CreateTimerRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String className;
	private final String methodName;
	private final long threshold;

	CreateTimerRequest(String serviceId, String className, String methodName, long threshold) {
		super(serviceId);

		this.className = className;
		this.methodName = methodName;
		this.threshold = threshold;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/timers";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Map<String, String> map = ImmutableMap.of("class_name", JsonUtil.stringify(className), "method_name",
				JsonUtil.stringify(methodName), "threshold", Long.toString(threshold));

		return JsonUtil.createSimpleJson(map, false).getBytes(StandardCharsets.UTF_8);
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

		public CreateTimerRequest build() {
			validate();

			return new CreateTimerRequest(serviceId, className, methodName, threshold);
		}
	}
}
