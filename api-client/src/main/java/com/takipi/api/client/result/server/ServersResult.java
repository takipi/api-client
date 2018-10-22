package com.takipi.api.client.result.server;

import java.util.List;

import com.takipi.api.client.data.server.SummarizedServer;
import com.takipi.api.core.result.intf.ApiResult;

public class ServersResult implements ApiResult {
	public List<SummarizedServer> servers;
}
