package com.takipi.common.api.result.deployment;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class DeploymentsResult implements ApiResult {
	public List<SummarizedDeployment> deployments;

	public static class SummarizedDeployment {
		public String name;
	}
}
