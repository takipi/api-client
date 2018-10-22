package com.takipi.api.client.result.service;

import java.util.List;

import com.takipi.api.client.data.service.SummarizedService;
import com.takipi.api.core.result.intf.ApiResult;

public class ServicesResult implements ApiResult {
	public List<SummarizedService> services;
}
