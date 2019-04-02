package com.takipi.api.client.util.regression;

import com.takipi.common.util.Pair;
import org.joda.time.DateTime;

import java.util.Map;

public class DeploymentTimespan
{
	private Map<String, Pair<DateTime, DateTime>> deploymentLifetime;
	private Pair<DateTime, DateTime> activeWindow;
	
	public DeploymentTimespan(Map<String, Pair<DateTime, DateTime>> deploymentLifetime,
							  Pair<DateTime, DateTime> activeWindow)
	{
		this.deploymentLifetime = deploymentLifetime;
		this.activeWindow = activeWindow;
	}
	
	public Pair<DateTime, DateTime> getActiveWindow() {
		return activeWindow;
	}
	
	public Map<String, Pair<DateTime, DateTime>> getDeploymentLifetime() {
		return deploymentLifetime;
	}
}
