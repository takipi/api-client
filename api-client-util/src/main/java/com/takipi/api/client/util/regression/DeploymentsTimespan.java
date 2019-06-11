package com.takipi.api.client.util.regression;

import com.takipi.common.util.Pair;
import org.joda.time.DateTime;

import java.util.Map;

public class DeploymentsTimespan
{
	private Map<String, Pair<DateTime, DateTime>> deploymentsLifetimeMap;
	private Pair<DateTime, DateTime> activeWindow;
	
	public DeploymentsTimespan(Map<String, Pair<DateTime, DateTime>> deploymentsLifetimeMap,
							   Pair<DateTime, DateTime> activeWindow) {
		this.deploymentsLifetimeMap = deploymentsLifetimeMap;
		this.activeWindow = activeWindow;
	}
	
	public Pair<DateTime, DateTime> getActiveWindow() {
		return activeWindow;
	}
	
	public Map<String, Pair<DateTime, DateTime>> getDeploymentsLifetimeMap() {
		return deploymentsLifetimeMap;
	}
}
