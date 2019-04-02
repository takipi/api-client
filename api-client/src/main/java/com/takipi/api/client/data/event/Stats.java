package com.takipi.api.client.data.event;

import com.takipi.api.client.result.event.IApiStats;

public class Stats implements IApiStats
{
	public long hits;
	public long invocations;
	
	public String machine_name;
	public String application_name;
	public String deployment_name;
	
	public Stats(Stats stats) {
		this.hits = stats.hits;
		this.invocations = stats.invocations;
		
		this.machine_name = stats.machine_name;
		this.application_name = stats.application_name;
		this.deployment_name = stats.deployment_name;
	}
	
	public Stats() {
	}
	
	@Override
	public Object clone() {
		return new Stats(this);
	}
}
