package com.takipi.api.client.result.event;

import com.google.common.collect.Lists;
import com.takipi.api.client.data.event.Stats;

import java.util.List;

public class MainStats extends ApiBaseStats {
	public List<Stats> contributors = Lists.newArrayList();
	
	public MainStats() { }
	
	public MainStats(MainStats mainStats) {
		this.hits = mainStats.hits;
		this.invocations = mainStats.invocations;
		
		this.contributors = Lists.newArrayList();
		
		for (Stats stat : mainStats.contributors) {
			this.contributors.add((Stats) stat.clone());
		}
	}
	
	@Override
	public Object clone() {
		return new MainStats(this);
	}
}
