package com.takipi.api.client.request;

import com.takipi.api.client.util.validation.ValidationUtil;

public abstract class ServiceRequest extends BaseRequest {
	public final String serviceId;

	protected ServiceRequest(String serviceId) {
		this.serviceId = serviceId;
	}

	protected String baseUrlPath() {
		return "/services/" + serviceId;
	}

	public static class Builder {
		protected Builder() {

		}

		protected String serviceId;

		public Builder setServiceId(String serviceId) {
			this.serviceId = serviceId;

			return this;
		}

		protected void validate() {
			if (!ValidationUtil.isLegalServiceId(serviceId)) {
				throw new IllegalArgumentException("Illegal service id - " + serviceId);
			}
		}
	}
}
