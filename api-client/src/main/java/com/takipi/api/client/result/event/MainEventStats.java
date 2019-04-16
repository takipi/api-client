package com.takipi.api.client.result.event;

import com.google.common.collect.Lists;
import com.takipi.api.client.data.event.Stats;

import java.util.List;

public class MainEventStats extends ApiBaseStats {
	public List<Stats> contributors = Lists.newArrayList();
	
	public MainEventStats() { }
	
	public MainEventStats(MainEventStats mainEventStats) {
		this.hits = mainEventStats.hits;
		this.invocations = mainEventStats.invocations;
		
		this.contributors = Lists.newArrayList();
		
		for (Stats stat : mainEventStats.contributors) {
			this.contributors.add((Stats) stat.clone());
		}
	}
	
	@Override
	public Object clone() {
		return new MainEventStats(this);
	}
}
