package com.takipi.common.api.request;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.takipi.common.api.util.ValidationUtil;

public abstract class TimeframeRequest extends ServiceRequest {
	public final String viewId;
	public final String from;
	public final String to;
	public final Collection<String> servers;
	public final Collection<String> apps;
	public final Collection<String> deployments;

	protected TimeframeRequest(String serviceId, String viewId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId);

		this.viewId = viewId;
		this.from = from;
		this.to = to;
		this.servers = servers;
		this.apps = apps;
		this.deployments = deployments;
	}

	protected String[] buildParams() throws UnsupportedEncodingException {
		String[] params = new String[paramsCount()];

		fillParams(params, 0);

		return params;
	}

	protected int paramsCount() {
		// 2 is reserved space for the from and to.
		//
		return (2 + servers.size() + apps.size() + deployments.size());
	}

	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = startIndex;

		params[index++] = "from=" + encode(from);
		params[index++] = "to=" + encode(to);

		for (String server : servers) {
			params[index++] = "server=" + encode(server);
		}

		for (String app : apps) {
			params[index++] = "app=" + encode(app);
		}

		for (String deployment : deployments) {
			params[index++] = "deployment=" + encode(deployment);
		}

		return index;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		protected String viewId;
		protected String from;
		protected String to;
		protected Collection<String> servers;
		protected Collection<String> apps;
		protected Collection<String> deployments;

		protected Builder() {
			this.servers = Sets.newHashSet();
			this.apps = Sets.newHashSet();
			this.deployments = Sets.newHashSet();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setViewId(String viewId) {
			this.viewId = viewId;

			return this;
		}

		public Builder setFrom(String from) {
			this.from = from;

			return this;
		}

		public Builder setTo(String to) {
			this.to = to;

			return this;
		}

		public Builder addServer(String server) {
			if (Strings.isNullOrEmpty(server)) {
				throw new IllegalArgumentException();
			}

			this.servers.add(server);

			return this;
		}

		public Builder addApp(String app) {
			if (Strings.isNullOrEmpty(app)) {
				throw new IllegalArgumentException();
			}

			this.apps.add(app);

			return this;
		}

		public Builder addDeployment(String deployment) {
			if (Strings.isNullOrEmpty(deployment)) {
				throw new IllegalArgumentException();
			}

			this.deployments.add(deployment);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalViewId(viewId)) {
				throw new IllegalArgumentException("Illegal view id - " + viewId);
			}

			try {
				DateTime.parse(from);
			} catch (Exception e) {
				throw new IllegalArgumentException("Illegal from time - " + from, e);
			}

			try {
				DateTime.parse(to);
			} catch (Exception e) {
				throw new IllegalArgumentException("Illegal to time - " + to, e);
			}
		}
	}
}
