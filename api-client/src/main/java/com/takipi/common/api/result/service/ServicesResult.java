package com.takipi.common.api.result.service;

import java.util.List;

import com.takipi.common.api.data.service.SummarizedService;
import com.takipi.common.api.result.intf.ApiResult;

public class ServicesResult implements ApiResult {
	public List<SummarizedService> services;
}
