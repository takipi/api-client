package com.takipi.api.client.request.alert;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;

public class CustomAlertRequest extends AlertRequest {
	private final String title;
	private final String body;

	private final List<AlertLink> links;

	protected CustomAlertRequest(String serviceId, String viewId, String title, String body, List<AlertLink> links) {
		super(serviceId, viewId);

		this.title = title;
		this.body = body;
		this.links = links;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/custom-alert";
	}

	@Override
	public String postData() {
		Map<String, String> map = Maps.newHashMap();

		if (!Strings.isNullOrEmpty(title)) {
			map.put("title", JsonUtil.stringify(title));
		}

		map.put("body", JsonUtil.stringify(body));

		if (!CollectionUtil.safeIsEmpty(links)) {
			List<String> jsons = Lists.newArrayListWithCapacity(links.size());

			for (AlertLink link : links) {
				jsons.add(link.toJson());
			}

			map.put("links", JsonUtil.createSimpleJson(jsons, false));
		}

		return JsonUtil.createSimpleJson(map, false);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends AlertRequest.Builder {
		private String title;
		private String body;
		private final List<AlertLink> links;

		Builder() {
			this.links = Lists.newArrayList();
		}

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

		public Builder setTitle(String title) {
			this.title = title;

			return this;
		}

		public Builder setBody(String body) {
			this.body = body;

			return this;
		}

		public Builder addLink(String linkText, String link) {
			return addLink(null, linkText, null, link);
		}

		public Builder addLink(String preLinkText, String linkText, String postLinkText, String link) {
			if (Strings.isNullOrEmpty(linkText)) {
				throw new IllegalArgumentException("Empty link text");
			}

			if (Strings.isNullOrEmpty(link)) {
				throw new IllegalArgumentException("Empty link");
			}

			this.links.add(AlertLink.create(preLinkText, linkText, postLinkText, link));

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (Strings.isNullOrEmpty(body)) {
				throw new IllegalArgumentException("Empty alert body");
			}
		}

		public CustomAlertRequest build() {
			validate();

			return new CustomAlertRequest(serviceId, viewId, title, body, links);
		}
	}

	static class AlertLink {
		final String pre_link_text;
		final String link_text;
		final String post_link_text;
		final String link;

		private AlertLink(String preLinkText, String linkText, String postLinkText, String link) {
			this.pre_link_text = preLinkText;
			this.link_text = linkText;
			this.post_link_text = postLinkText;
			this.link = link;
		}

		public String toJson() {
			return (new Gson()).toJson(this);
		}

		static AlertLink create(String preLinkText, String linkText, String postLinkText, String link) {
			return new AlertLink(preLinkText, linkText, postLinkText, link);
		}
	}
}
