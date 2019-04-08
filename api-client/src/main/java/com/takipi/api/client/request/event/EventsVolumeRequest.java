package com.takipi.api.client.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.api.client.request.ViewTimeframeRequest;
import com.takipi.api.client.result.event.EventsResult;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class EventsVolumeRequest extends ViewTimeframeRequest implements ApiGetRequest<EventsResult> {
	public final VolumeType volumeType;
	public final boolean includeStacktrace;

	EventsVolumeRequest(String serviceId, String viewId, VolumeType volumeType, String from, String to, boolean raw,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments,
			boolean includeStacktrace) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);

		this.volumeType = volumeType;
		this.includeStacktrace = includeStacktrace;
	}

	@Override
	public Class<EventsResult> resultClass() {
		return EventsResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/events/";
	}

	@Override
	protected int paramsCount() {
		// One slot for the volume type and one for the include stacktrace.
		//
		return super.paramsCount() + 2;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stats=" + volumeType.toString();
		params[index++] = "stacktrace=" + Boolean.toString(includeStacktrace);

		return index;
	}

	@Override
	public String[] queryParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ViewTimeframeRequest.Builder {
		private VolumeType volumeType;
		private boolean includeStacktrace;

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		@Override
		public Builder setViewId(String viewId) {
			super.setViewId(viewId);

			return this;
		}

		@Override
		public Builder setRaw(boolean raw) {
			super.setRaw(raw);

			return this;
		}

		public Builder setVolumeType(VolumeType volumeType) {
			this.volumeType = volumeType;

			return this;
		}

		@Override
		public Builder setFrom(String from) {
			super.setFrom(from);

			return this;
		}

		@Override
		public Builder setTo(String to) {
			super.setTo(to);

			return this;
		}

		@Override
		public Builder addServer(String server) {
			super.addServer(server);

			return this;
		}

		@Override
		public Builder addApp(String app) {
			super.addApp(app);

			return this;
		}

		@Override
		public Builder addDeployment(String deployment) {
			super.addDeployment(deployment);

			return this;
		}

		public Builder setIncludeStacktrace(boolean includeStacktrace) {
			this.includeStacktrace = includeStacktrace;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (volumeType == null) {
				throw new IllegalArgumentException("Missing volume type");
			}
		}

		public EventsVolumeRequest build() {
			validate();

			return new EventsVolumeRequest(serviceId, viewId, volumeType, from, to, raw, servers, apps, deployments,
					includeStacktrace);
		}
	}
}
