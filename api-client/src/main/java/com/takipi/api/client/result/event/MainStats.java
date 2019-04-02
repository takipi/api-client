package com.takipi.api.client.result.event;

import com.google.common.collect.Lists;
import com.takipi.api.client.data.event.Stats;

import java.util.List;

public class MainStats implements IApiStats
{
	public long hits;
	public long invocations;
	
	public List<Stats> contributors = Lists.newArrayList();
	
	@Override
	public Object clone()
	{
		MainStats mainStats = new MainStats();
		
		mainStats.hits = this.hits;
		mainStats.invocations = this.invocations;
		mainStats.contributors = Lists.newArrayList();
		
		for (Stats stat : this.contributors)
		{
			mainStats.contributors.add((Stats) stat.clone());
		}
		
		return mainStats;
	}
}
