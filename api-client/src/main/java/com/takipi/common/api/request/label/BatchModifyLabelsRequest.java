package com.takipi.common.api.request.label;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.takipi.common.api.consts.ApiConstants;
import com.takipi.common.api.request.ServiceRequest;
import com.takipi.common.api.request.intf.ApiPostRequest;
import com.takipi.common.api.result.EmptyResult;
import com.takipi.common.api.util.JsonUtil;
import com.takipi.common.api.util.ValidationUtil;

public class BatchModifyLabelsRequest extends ServiceRequest implements ApiPostRequest<EmptyResult> {
	private final Collection<Modification> modifications;

	BatchModifyLabelsRequest(String serviceId, Collection<Modification> modifications) {
		super(serviceId);

		this.modifications = modifications;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/events/labels";
	}

	@Override
	public byte[] postData() throws UnsupportedEncodingException {
		Collection<String> itemJsons = Lists.newArrayListWithCapacity(modifications.size());

		for (Modification modification : modifications) {
			String itemJson = JsonUtil
					.createSimpleJson(ImmutableMap.of("event_id", JsonUtil.stringify(modification.eventId), "add",
							JsonUtil.createSimpleJson(modification.addLabels, true), "remove",
							JsonUtil.createSimpleJson(modification.removeLabels, true)));

			itemJsons.add(itemJson);
		}

		return JsonUtil.createSimpleJson(ImmutableMap.of("items", JsonUtil.createSimpleJson(itemJsons, false)))
				.getBytes(ApiConstants.UTF8_ENCODING);
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	static class Modification {
		final String eventId;
		final Collection<String> addLabels;
		final Collection<String> removeLabels;

		private Modification(String eventId, Collection<String> addLabels, Collection<String> removeLabels) {
			this.eventId = eventId;
			this.addLabels = addLabels;
			this.removeLabels = removeLabels;
		}

		static Modification of(String eventId, Collection<String> addLabels, Collection<String> removeLabels) {
			return new Modification(eventId, addLabels, removeLabels);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private final Map<String, Modification> modifications;

		Builder() {
			this.modifications = Maps.newHashMap();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder addLabelModifications(String eventId, Collection<String> addLabels,
				Collection<String> removeLabels) {
			this.modifications.put(eventId, Modification.of(eventId, addLabels, removeLabels));

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (modifications.isEmpty()) {
				throw new IllegalArgumentException("No modifications provided");
			}

			for (Modification modification : modifications.values()) {
				if (!ValidationUtil.isLegalEventId(modification.eventId)) {
					throw new IllegalArgumentException("Illegal event id - " + modification.eventId);
				}

				if ((modification.addLabels.size() + modification.removeLabels.size()) == 0) {
					throw new IllegalArgumentException("Must modify labels of " + modification.eventId);
				}
			}
		}

		public BatchModifyLabelsRequest build() {
			validate();

			return new BatchModifyLabelsRequest(serviceId, modifications.values());
		}
	}
}
