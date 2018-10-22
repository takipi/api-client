package com.takipi.api.client.util.label;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.label.Label;
import com.takipi.api.client.request.label.CreateLabelRequest;
import com.takipi.api.client.request.label.LabelsRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.label.LabelsResult;
import com.takipi.api.core.url.UrlClient.Response;

public class LabelUtil {
	public static Map<String, Label> getServiceLabels(ApiClient apiClient, String serviceId) {
		LabelsRequest viewsRequest = LabelsRequest.newBuilder().setServiceId(serviceId).build();

		Response<LabelsResult> labelsResponse = apiClient.get(viewsRequest);

		if ((labelsResponse.isBadResponse()) || (labelsResponse.data == null) || (labelsResponse.data.labels == null)) {
			System.err.println("Can't list labels");
			return Collections.emptyMap();
		}

		Map<String, Label> result = Maps.newHashMap();

		for (Label label : labelsResponse.data.labels) {
			result.put(label.name, label);
		}

		return result;
	}

	public static void createLabelsIfNotExists(ApiClient apiClient, String serviceId, String[] labelNames) {
		Map<String, Label> existingLabels = getServiceLabels(apiClient, serviceId);

		for (String labelName : labelNames) {
			if (existingLabels.containsKey(labelName)) {
				System.out.println("label " + labelName + " found");

				continue;
			}

			CreateLabelRequest createLabelRequest = CreateLabelRequest.newBuilder().setServiceId(serviceId)
					.setName(labelName).build();

			Response<EmptyResult> labelResponse = apiClient.post(createLabelRequest);

			if (labelResponse.isBadResponse()) {
				System.err.println("Cannot create label " + labelName);
			}
		}
	}
}
