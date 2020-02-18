package com.takipi.api.client.request.timer;

import java.util.Map;

import com.takipi.api.client.data.timer.Timer;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class CreateTimerRequest extends ServiceRequest implements ApiPostRequest<Timer> {
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
	public String postData() {
		Map<String, String> map = CollectionUtil.mapOf("class_name", JsonUtil.stringify(className), "method_name",
				JsonUtil.stringify(methodName), "threshold", Long.toString(threshold));

		return JsonUtil.createSimpleJson(map, false);
	}

	@Override
	public Class<Timer> resultClass() {
		return Timer.class;
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

			if (StringUtil.isNullOrEmpty(className)) {
				throw new IllegalArgumentException("Missing class name");
			}

			if (StringUtil.isNullOrEmpty(methodName)) {
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
