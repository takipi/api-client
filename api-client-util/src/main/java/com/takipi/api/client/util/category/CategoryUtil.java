package com.takipi.api.client.util.category;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.category.Category;
import com.takipi.api.client.request.category.CategoriesRequest;
import com.takipi.api.client.result.category.CategoriesResult;
import com.takipi.api.core.url.UrlClient.Response;

public class CategoryUtil {
	public static Category getServiceCategoryByName(ApiClient apiClient, String serviceId, String categoryName) {
		CategoriesRequest categoriesRequest = CategoriesRequest.newBuilder().setServiceId(serviceId).build();

		Response<CategoriesResult> createCategoryResponse = apiClient.get(categoriesRequest);

		if ((createCategoryResponse.isOK()) && (createCategoryResponse.data.categories != null)) {
			for (Category category : createCategoryResponse.data.categories) {
				if (categoryName.equals(category.name)) {
					return category;
				}
			}
		}

		return null;
	}
}
