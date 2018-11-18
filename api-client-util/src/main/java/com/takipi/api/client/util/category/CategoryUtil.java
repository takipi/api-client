package com.takipi.api.client.util.category;

import java.net.HttpURLConnection;

import com.google.common.base.Strings;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.category.Category;
import com.takipi.api.client.request.category.CategoriesRequest;
import com.takipi.api.client.request.category.CategoryAddViewRequest;
import com.takipi.api.client.request.category.CreateCategoryRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.category.CategoriesResult;
import com.takipi.api.client.result.category.CreateCategoryResult;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;

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
	

	public static String createCategory(String categoryName, String serviceId, ApiClient apiClient) {
		CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.newBuilder().setServiceId(serviceId)
				.setName(categoryName).setShared(true).build();

		Response<CreateCategoryResult> createCategoryResponse = apiClient.post(createCategoryRequest);

		if (createCategoryResponse.isOK()) {
			CreateCategoryResult createCategoryResult = createCategoryResponse.data;

			if ((createCategoryResult == null) || (Strings.isNullOrEmpty(createCategoryResult.category_id))) {
				throw new IllegalStateException("Failed creating category - " + categoryName);
			}

			return createCategoryResult.category_id;
		}

		if (createCategoryResponse.responseCode != HttpURLConnection.HTTP_CONFLICT) {
			throw new IllegalStateException("Failed creating category - " + categoryName);
		}

		CategoriesRequest getCategoriesRequest = CategoriesRequest.newBuilder().setServiceId(serviceId).build();

		Response<CategoriesResult> getCategoriesResponse = apiClient.get(getCategoriesRequest);

		if ((getCategoriesResponse.isBadResponse()) || (getCategoriesResponse.data == null)
				|| (CollectionUtil.safeIsEmpty(getCategoriesResponse.data.categories))) {
			throw new IllegalStateException("Failed getting category - " + categoryName);
		}

		for (Category category : getCategoriesResponse.data.categories) {
			if ((categoryName.equalsIgnoreCase(category.name)) && (!Strings.isNullOrEmpty(category.id))) {
				return category.id;
			}
		}

		throw new IllegalStateException("Failed getting category - " + categoryName);
	}

	public static void addViewToCategory(String categoryId, String viewId, String serviceId, ApiClient apiClient) {
		CategoryAddViewRequest categoryAddViewRequest = CategoryAddViewRequest.newBuilder().setServiceId(serviceId)
				.setCategoryId(categoryId).setViewId(viewId).build();

		Response<EmptyResult> createResponse = apiClient.post(categoryAddViewRequest);

		if (createResponse.isBadResponse()) {
			throw new IllegalStateException("Failed adding view " + viewId + " to category " + categoryId);
		}
	}
}
