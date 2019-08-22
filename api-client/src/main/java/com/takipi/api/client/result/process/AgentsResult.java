package com.takipi.api.client.result.process;

import java.util.List;

import com.takipi.api.client.data.process.Agent;
import com.takipi.api.core.result.intf.ApiResult;

public class AgentsResult implements ApiResult {
	public List<Agent> agents;
}
