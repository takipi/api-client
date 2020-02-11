package com.takipi.api.client.request.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class CustomAlertRequest extends AlertRequest {
	public final String title;
	public final String body;

	public final List<AlertLink> links;

	protected CustomAlertRequest(String serviceId, String viewId, String title, String body, List<AlertLink> links) {
		super(serviceId, viewId);

		this.title = title;
		this.body = body;
		this.links = new ArrayList<>(links);
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/custom-alert";
	}

	@Override
	public String postData() {
		Map<String, String> map = new HashMap<>();

		if (!StringUtil.isNullOrEmpty(title)) {
			map.put("title", JsonUtil.stringify(title));
		}

		map.put("body", JsonUtil.stringify(body));

		if (!CollectionUtil.safeIsEmpty(links)) {
			List<String> jsons = new ArrayList<>();

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
			this.links = new ArrayList<>();
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
			if (StringUtil.isNullOrEmpty(linkText)) {
				throw new IllegalArgumentException("Empty link text");
			}

			if (StringUtil.isNullOrEmpty(link)) {
				throw new IllegalArgumentException("Empty link");
			}

			return addLink(AlertLink.create(preLinkText, linkText, postLinkText, link));
		}

		public Builder addLink(AlertLink alertLink) {
			this.links.add(alertLink);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (StringUtil.isNullOrEmpty(body)) {
				throw new IllegalArgumentException("Empty alert body");
			}
		}

		public CustomAlertRequest build() {
			validate();

			return new CustomAlertRequest(serviceId, viewId, title, body, links);
		}
	}

	public static class AlertLink {
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

		public static AlertLink create(String preLinkText, String linkText, String postLinkText, String link) {
			return new AlertLink(preLinkText, linkText, postLinkText, link);
		}
	}
}
