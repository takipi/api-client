package com.takipi.api.client.request;

import java.util.Collection;

import com.takipi.api.client.util.validation.ValidationUtil;

public abstract class ViewTimeframeRequest extends TimeframeRequest {
	public final String viewId;

	protected ViewTimeframeRequest(String serviceId, String viewId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId, from, to, servers, apps, deployments);

		this.viewId = viewId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends TimeframeRequest.Builder {
		protected String viewId;

		public Builder setViewId(String viewId) {
			this.viewId = viewId;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalViewId(viewId)) {
				throw new IllegalArgumentException("Illegal view id - " + viewId);
			}
		}
	}
}
