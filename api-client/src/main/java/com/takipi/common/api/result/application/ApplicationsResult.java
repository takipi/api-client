package com.takipi.common.api.result.application;

import java.util.List;

import com.takipi.common.api.data.application.SummarizedApplication;
import com.takipi.common.api.result.intf.ApiResult;

public class ApplicationsResult implements ApiResult {
	public List<SummarizedApplication> applications;
}
