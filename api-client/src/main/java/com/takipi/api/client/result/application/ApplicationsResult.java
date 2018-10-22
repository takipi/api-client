package com.takipi.api.client.result.application;

import java.util.List;

import com.takipi.api.client.data.application.SummarizedApplication;
import com.takipi.api.core.result.intf.ApiResult;

public class ApplicationsResult implements ApiResult {
	public List<SummarizedApplication> applications;
}
