package com.takipi.common.api.result.server;

import java.util.List;

import com.takipi.common.api.data.server.SummarizedServer;
import com.takipi.common.api.result.intf.ApiResult;

public class ServersResult implements ApiResult {
	public List<SummarizedServer> servers;
}
