package com.takipi.api.client.request;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.DateTime;

import com.takipi.common.util.StringUtil;

public abstract class TimeframeRequest extends ServiceRequest {
	public final String from;
	public final String to;
	public final Collection<String> servers;
	public final Collection<String> apps;
	public final Collection<String> deployments;

	protected TimeframeRequest(String serviceId, String from, String to, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments) {
		super(serviceId);

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
		protected String from;
		protected String to;
		protected Collection<String> servers;
		protected Collection<String> apps;
		protected Collection<String> deployments;

		protected Builder() {
			this.servers = new HashSet<>();
			this.apps = new HashSet<>();
			this.deployments = new HashSet<>();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

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
			if (StringUtil.isNullOrEmpty(server)) {
				throw new IllegalArgumentException();
			}

			this.servers.add(server);

			return this;
		}

		public Builder addApp(String app) {
			if (StringUtil.isNullOrEmpty(app)) {
				throw new IllegalArgumentException();
			}

			this.apps.add(app);

			return this;
		}

		public Builder addDeployment(String deployment) {
			if (StringUtil.isNullOrEmpty(deployment)) {
				throw new IllegalArgumentException();
			}

			this.deployments.add(deployment);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

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
