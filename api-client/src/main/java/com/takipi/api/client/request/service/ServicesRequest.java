package com.takipi.api.client.request.service;

import com.takipi.api.client.request.BaseRequest;
import com.takipi.api.client.result.service.ServicesResult;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.common.util.StringUtil;

public class ServicesRequest extends BaseRequest implements ApiGetRequest<ServicesResult> {
	public final String serviceId;

	ServicesRequest(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public Class<ServicesResult> resultClass() {
		return ServicesResult.class;
	}

	@Override
	public String urlPath() {
		if (StringUtil.isNullOrEmpty(serviceId)) {
			return "/services";
		} else {
			return "/services/" + serviceId;
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String serviceId;

		Builder() {

		}

		public Builder setServiceId(String serviceId) {
			this.serviceId = serviceId;

			return this;
		}

		protected void validate() {
			// We allow not specifying a service id, but if specified, must be legal
			//
			if ((!StringUtil.isNullOrEmpty(serviceId)) && (!ValidationUtil.isLegalServiceId(serviceId))) {
				throw new IllegalArgumentException("Illegal service id - " + serviceId);
			}
		}

		public ServicesRequest build() {
			validate();

			return new ServicesRequest(serviceId);
		}
	}
}
