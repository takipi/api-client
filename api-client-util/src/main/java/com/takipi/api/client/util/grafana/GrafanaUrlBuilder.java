package com.takipi.api.client.util.grafana;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.takipi.api.client.functions.input.FunctionInput;
import com.takipi.api.core.consts.ApiConstants;
import com.takipi.common.util.CollectionUtil;

public class GrafanaUrlBuilder {
	private static final String BASE_TEMPLATE = "%s/d/%s/%s?ordId=1";

	private static final String ENVS_PARAM = "var-environments";
	private static final String FROM_PARAM = "from";
	private static final String TO_PARAM = "to";
	private static final String APPS_PARAM = "var-applications";
	private static final String DEPS_PARAM = "var-deployments";
	private static final String MACHINES_PARAM = "var-servers";

	private static final String ALL_VALUE = "All";

	private final String grafanaHostname;
	private final GrafanaDashboard dashboard;
	private final Map<String, String> urlParams;
	private final Map<String, Collection<String>> urlListParams;

	private GrafanaUrlBuilder(String grafanaHostname, GrafanaDashboard dashboard) {
		this.grafanaHostname = grafanaHostname;
		this.dashboard = dashboard;
		this.urlParams = Maps.newHashMap();
		this.urlListParams = Maps.newHashMap();
	}

	public GrafanaUrlBuilder withEnrivonment(String environmentName, String environmentId) {
		if (Strings.isNullOrEmpty(environmentId)) {
			urlParams.remove(ENVS_PARAM);

			return this;
		}

		if (Strings.isNullOrEmpty(environmentName)) {
			urlParams.put(ENVS_PARAM, environmentId);
		} else {
			urlParams.put(ENVS_PARAM, environmentName + FunctionInput.SERVICE_SEPERATOR + environmentId);
		}

		return this;
	}

	public GrafanaUrlBuilder withParam(String param, String value) {
		if (Strings.isNullOrEmpty(value)) {
			urlParams.remove(param);

			return this;
		}

		urlParams.put(param, value);

		return this;
	}

	public GrafanaUrlBuilder withParam(String param, DateTime value) {
		if (value == null) {
			urlParams.remove(param);

			return this;
		}

		urlParams.put(param, String.valueOf(value.getMillis()));

		return this;
	}

	public GrafanaUrlBuilder withListParam(String param, String value) {
		if (Strings.isNullOrEmpty(value)) {
			return this;
		}

		Collection<String> listParams = urlListParams.get(param);

		if (listParams == null) {
			listParams = Sets.newHashSet();
			urlListParams.put(param, listParams);
		}

		listParams.add(value);

		return this;
	}

	public GrafanaUrlBuilder withListParams(String param, Collection<String> values) {
		if (CollectionUtil.safeIsEmpty(values)) {
			return this;
		}

		for (String value : values) {
			withListParam(param, value);
		}

		return this;
	}

	public GrafanaUrlBuilder withoutListParam(String param, String value) {
		if (Strings.isNullOrEmpty(value)) {
			return this;
		}

		Collection<String> listParams = urlListParams.get(param);

		if (listParams == null) {
			return this;
		}

		listParams.remove(value);

		return this;
	}

	public GrafanaUrlBuilder withFrom(String from) {
		return withParam(FROM_PARAM, from);
	}

	public GrafanaUrlBuilder withFrom(DateTime from) {
		return withParam(FROM_PARAM, from);
	}

	public GrafanaUrlBuilder withTo(String to) {
		return withParam(FROM_PARAM, to);
	}

	public GrafanaUrlBuilder withTo(DateTime to) {
		return withParam(TO_PARAM, to);
	}

	public GrafanaUrlBuilder withApplication(String application) {
		return withoutListParam(APPS_PARAM, ALL_VALUE).withListParam(APPS_PARAM, application);
	}

	public GrafanaUrlBuilder withApplications(Collection<String> applications) {
		if (CollectionUtil.safeIsEmpty(applications)) {
			return this;
		}

		return withoutListParam(APPS_PARAM, ALL_VALUE).withListParams(APPS_PARAM, applications);
	}

	public GrafanaUrlBuilder withDeployment(String deployment) {
		return withoutListParam(DEPS_PARAM, ALL_VALUE).withListParam(DEPS_PARAM, deployment);
	}

	public GrafanaUrlBuilder withDeployments(Collection<String> deployments) {
		if (CollectionUtil.safeIsEmpty(deployments)) {
			return this;
		}

		return withoutListParam(DEPS_PARAM, ALL_VALUE).withListParams(DEPS_PARAM, deployments);
	}

	public GrafanaUrlBuilder withMachine(String machine) {
		return withoutListParam(MACHINES_PARAM, ALL_VALUE).withListParam(MACHINES_PARAM, machine);
	}

	public GrafanaUrlBuilder withMachines(Collection<String> machines) {
		if (CollectionUtil.safeIsEmpty(machines)) {
			return this;
		}

		return withoutListParam(MACHINES_PARAM, ALL_VALUE).withListParams(MACHINES_PARAM, machines);
	}

	public String buildUrl() {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format(BASE_TEMPLATE, grafanaHostname, dashboard.dashboardId, dashboard.dashboardName));

		for (Map.Entry<String, String> entry : urlParams.entrySet()) {
			String param = entry.getKey();
			String value = safeUrlEncode(entry.getValue());

			builder.append('&');
			builder.append(param);

			if (!Strings.isNullOrEmpty(value)) {
				builder.append('=');
				builder.append(value);
			}
		}

		for (Entry<String, Collection<String>> entry : urlListParams.entrySet()) {
			String param = entry.getKey();
			Collection<String> values = entry.getValue();

			for (String rawValue : values) {
				String value = safeUrlEncode(rawValue);

				if (!Strings.isNullOrEmpty(value)) {
					builder.append('&');
					builder.append(param);
					builder.append('=');
					builder.append(value);
				}
			}
		}

		return builder.toString();
	}

	private static String safeUrlEncode(String value) {
		if (Strings.isNullOrEmpty(value)) {
			return value;
		}

		try {
			return URLEncoder.encode(value, ApiConstants.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}

	public static GrafanaUrlBuilder create(String grafanaHostname, GrafanaDashboard dashboard) {
		return new GrafanaUrlBuilder(grafanaHostname, dashboard).withListParam(APPS_PARAM, ALL_VALUE)
				.withListParam(DEPS_PARAM, ALL_VALUE).withListParam(MACHINES_PARAM, ALL_VALUE);
	}
}
