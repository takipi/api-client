package com.takipi.api.client.result.deployment;

import java.util.List;

import com.takipi.api.client.data.deployment.SummarizedDeployment;
import com.takipi.api.core.result.intf.ApiResult;

public class DeploymentsResult implements ApiResult {
	public List<SummarizedDeployment> deployments;
}
