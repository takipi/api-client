package com.takipi.common.api.request.event;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;
import com.takipi.common.api.util.JsonUtil;
import com.takipi.common.api.util.ValidationUtil;

public class EventModifyLabelsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final String eventId;
	private final Collection<String> addLabels;
	private final Collection<String> removeLabels;

	EventModifyLabelsRequest(String serviceId, String eventId, Collection<String> addLabels,
			Collection<String> removeLabels) {
		super(serviceId);

		this.eventId = eventId;
		this.addLabels = addLabels;
		this.removeLabels = removeLabels;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/" + eventId + "/labels";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		return JsonUtil.createSimpleJson(ImmutableMap.of("add", JsonUtil.createSimpleJson(addLabels, true), "remove",
				JsonUtil.createSimpleJson(removeLabels, true))).getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String eventId;

		private final Collection<String> addLabels;
		private final Collection<String> removeLabels;

		Builder() {
			this.addLabels = Sets.newHashSet();
			this.removeLabels = Sets.newHashSet();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setEventId(String eventId) {
			this.eventId = eventId;

			return this;
		}

		public Builder addLabel(String label) {
			this.addLabels.add(label);
			this.removeLabels.remove(label);

			return this;
		}

		public Builder addLabels(Collection<String> labels) {
			this.addLabels.addAll(labels);
			this.removeLabels.removeAll(labels);

			return this;
		}

		public Builder removeLabel(String label) {
			this.addLabels.remove(label);
			this.removeLabels.add(label);

			return this;
		}

		public Builder removeLabels(Collection<String> labels) {
			this.addLabels.removeAll(labels);
			this.removeLabels.addAll(labels);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalEventId(eventId)) {
				throw new IllegalArgumentException("Illegal event id - " + eventId);
			}

			if ((this.addLabels.size() + this.removeLabels.size()) == 0) {
				throw new IllegalArgumentException("Must modify labels");
			}
		}

		public EventModifyLabelsRequest build() {
			validate();

			return new EventModifyLabelsRequest(serviceId, eventId, addLabels, removeLabels);
		}
	}
}
