package com.takipi.api.client.result.view;

import com.takipi.api.client.data.view.ViewFilters;
import com.takipi.api.core.result.intf.ApiResult;

public class ViewResult implements ApiResult {
	public String id;
	public String name;
	public String description;
	public boolean shared;
	public boolean admin;
	public boolean immutable;
	public ViewFilters filters;
}
