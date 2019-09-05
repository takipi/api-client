package com.takipi.api.client.result.process;

import java.util.List;

import com.takipi.api.client.data.process.Collector;
import com.takipi.api.core.result.intf.ApiResult;

public class CollectorsResult implements ApiResult {
	public List<Collector> collectors;
}
