package com.takipi.common.api.request.view;

import java.io.UnsupportedEncodingException;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.data.view.ViewInfo;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.view.CreateViewResult;
import com.takipi.common.api.util.CollectionUtil;

public class CreateViewRequest extends ServiceRequest implements ApiPostRequest<CreateViewResult> {
	private final ViewInfo viewInfo;

	CreateViewRequest(String serviceId, ViewInfo viewInfo) {
		super(serviceId);

		this.viewInfo = viewInfo;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		return (new Gson()).toJson(viewInfo).getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<CreateViewResult> resultClass() {
		return CreateViewResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private ViewInfo viewInfo;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setViewInfo(ViewInfo viewInfo) {
			this.viewInfo = viewInfo;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (viewInfo == null) {
				throw new IllegalArgumentException("Missing view info");
			}

			if (Strings.isNullOrEmpty(viewInfo.name)) {
				throw new IllegalArgumentException("Missing name");
			}

			if (!Strings.isNullOrEmpty(viewInfo.id)) {
				throw new IllegalArgumentException("Can't provide id for view creation");
			}

			if (viewInfo.filters == null) {
				throw new IllegalArgumentException("Missing filters");
			}

			if ((viewInfo.filters.first_seen == null) && (CollectionUtil.safeIsEmpty(viewInfo.filters.labels))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.event_type))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.event_name))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.event_location))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.event_package))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.entry_point))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.servers))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.apps))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.deployments))
					&& (CollectionUtil.safeIsEmpty(viewInfo.filters.introduced_by))
					&& (Strings.isNullOrEmpty(viewInfo.filters.search))) {
				throw new IllegalArgumentException("Empty filters");
			}
		}

		public CreateViewRequest build() {
			validate();

			return new CreateViewRequest(serviceId, viewInfo);
		}
	}
}
