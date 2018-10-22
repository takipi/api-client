package com.takipi.api.client.result.category;

import java.util.List;

import com.takipi.api.client.data.category.Category;
import com.takipi.api.core.result.intf.ApiResult;

public class CategoriesResult implements ApiResult {
	public List<Category> categories;
}
