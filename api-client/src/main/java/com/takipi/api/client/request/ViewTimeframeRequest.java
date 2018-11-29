package com.takipi.api.client.request;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.util.validation.ValidationUtil;

public abstract class ViewTimeframeRequest extends TimeframeRequest {
	public final String viewId;
	public final boolean raw;

	protected ViewTimeframeRequest(String serviceId, String viewId, String from, String to, boolean raw,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments) {
		super(serviceId, from, to, servers, apps, deployments);

		this.viewId = viewId;
		this.raw = raw;
	}

	@Override
	protected int paramsCount() {
		// One slot for "raw".
		//
		return super.paramsCount() + 1;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "raw=" + String.valueOf(raw);

		return index;
	}

	public static class Builder extends TimeframeRequest.Builder {
		protected String viewId;
		protected boolean raw;

		public Builder setViewId(String viewId) {
			this.viewId = viewId;

			return this;
		}

		public Builder setRaw(boolean raw) {
			this.raw = raw;

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
