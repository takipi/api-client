package com.takipi.common.api.request.volume;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.takipi.common.api.request.TimeframeRequest;
import com.takipi.common.api.request.intf.ApiGetRequest;
import com.takipi.common.api.result.volume.EventsVolumeResult;
import com.takipi.common.api.util.ValidationUtil.VolumeType;

public class EventsVolumeRequest extends TimeframeRequest implements ApiGetRequest<EventsVolumeResult> {
	public final VolumeType volumeType;

	EventsVolumeRequest(String serviceId, String viewId, VolumeType volumeType, String from, String to,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments) {
		super(serviceId, viewId, from, to, servers, apps, deployments);

		this.volumeType = volumeType;
	}

	@Override
	public Class<EventsVolumeResult> resultClass() {
		return EventsVolumeResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/views/" + viewId + "/events/";
	}

	@Override
	protected int paramsCount() {
		// One slot for the volume type.
		//
		return super.paramsCount() + 1;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "stats=" + volumeType.toString();

		return index;
	}

	@Override
	public String[] getParams() throws UnsupportedEncodingException {
		return buildParams();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends TimeframeRequest.Builder {
		private VolumeType volumeType;

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

		public EventsVolumeRequest build() {
			validate();

			return new EventsVolumeRequest(serviceId, viewId, volumeType, from, to, servers, apps, deployments);
		}
	}
}
