package com.takipi.common.api.result.category;

import java.util.List;

import com.takipi.common.api.data.category.Category;
import com.takipi.common.api.result.intf.ApiResult;

public class CategoriesResult implements ApiResult {
	public List<Category> categories;
}
