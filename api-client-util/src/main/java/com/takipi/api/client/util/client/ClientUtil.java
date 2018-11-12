package com.takipi.api.client.util.client;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.application.SummarizedApplication;
import com.takipi.api.client.data.deployment.SummarizedDeployment;
import com.takipi.api.client.data.server.SummarizedServer;
import com.takipi.api.client.data.service.SummarizedService;
import com.takipi.api.client.request.application.ApplicationsRequest;
import com.takipi.api.client.request.deployment.DeploymentsRequest;
import com.takipi.api.client.request.server.ServersRequest;
import com.takipi.api.client.request.service.ServicesRequest;
import com.takipi.api.client.result.application.ApplicationsResult;
import com.takipi.api.client.result.deployment.DeploymentsResult;
import com.takipi.api.client.result.server.ServersResult;
import com.takipi.api.client.result.service.ServicesResult;
import com.takipi.api.core.url.UrlClient.Response;

public class ClientUtil {

	public static List<String> getDeployments(ApiClient apiClient, String serviceId) {
		return getDeployments(apiClient, serviceId, false);
	}

	public static List<String> getDeployments(ApiClient apiClient, String serviceId, boolean active) {

		DeploymentsRequest request = DeploymentsRequest.newBuilder().setServiceId(serviceId).setActive(active).build();

		Response<DeploymentsResult> response = apiClient.get(request);

		if ((response.isBadResponse()) || (response.data == null)) {
			throw new IllegalStateException(
					"Could not acquire deployments for service " + serviceId + " . Error " + response.responseCode);
		}

		if (response.data.deployments == null) {
			return Collections.emptyList();
		}

		List<String> result = Lists.newArrayListWithCapacity(response.data.deployments.size());

		for (SummarizedDeployment deployment : response.data.deployments) {
			result.add(deployment.name);
		}

		return result;
	}

	public static List<String> getApplications(ApiClient apiClient, String serviceId) {

		ApplicationsRequest request = ApplicationsRequest.newBuilder().setServiceId(serviceId).build();

		Response<ApplicationsResult> response = apiClient.get(request);

		if ((response.isBadResponse()) || (response.data == null)) {
			throw new IllegalStateException(
					"Could not acquire applications for service " + serviceId + " . Error " + response.responseCode);
		}

		if (response.data.applications == null) {
			return Collections.emptyList();
		}

		List<String> result = Lists.newArrayListWithCapacity(response.data.applications.size());

		for (SummarizedApplication app : response.data.applications) {
			result.add(app.name);
		}

		return result;
	}

	public static List<String> getServers(ApiClient apiClient, String serviceId) {
		ServersRequest srvRequest = ServersRequest.newBuilder().setServiceId(serviceId).build();

		Response<ServersResult> response = apiClient.get(srvRequest);

		if ((response.isBadResponse()) || (response.data == null)) {
			throw new IllegalStateException(
					"Could not acquire servers for service " + serviceId + " . Error " + response.responseCode);
		}

		if (response.data.servers == null) {
			return Collections.emptyList();
		}

		List<String> result = Lists.newArrayListWithCapacity(response.data.servers.size());

		for (SummarizedServer server : response.data.servers) {
			result.add(server.name);
		}

		return result;
	}

	public static List<SummarizedService> getEnvironments(ApiClient apiClient) {
		ServicesRequest request = ServicesRequest.newBuilder().build();

		Response<ServicesResult> response = apiClient.get(request);

		if ((response.isBadResponse()) || (response.data == null)) {
			throw new IllegalStateException("Could not acquire services. Error " + response.responseCode);
		}

		if (response.data.services == null) {
			return Collections.emptyList();
		}

		return response.data.services;
	}
}
