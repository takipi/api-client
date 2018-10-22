package com.takipi.api.client.result.view;

import java.util.List;

import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.core.result.intf.ApiResult;

public class ViewsResult implements ApiResult {
	public List<SummarizedView> views;
}
