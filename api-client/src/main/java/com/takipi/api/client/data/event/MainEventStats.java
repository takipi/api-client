package com.takipi.api.client.data.event;

import java.util.List;

import com.google.common.collect.Lists;
import com.takipi.common.util.CollectionUtil;

public class MainEventStats extends BaseStats {
	public List<Stats> contributors;

	@Override
	public MainEventStats clone() {
		MainEventStats result = new MainEventStats();

		result.hits = this.hits;
		result.invocations = this.invocations;

		if (!CollectionUtil.safeIsEmpty(this.contributors)) {
			result.contributors = Lists.newArrayListWithExpectedSize(this.contributors.size());

			for (Stats stats : this.contributors) {
				result.contributors.add(stats.clone());
			}
		}

		return result;
	}
	
	public static MainEventStats fromStats(Stats stats) {
		MainEventStats eventStats = new MainEventStats();
		
		eventStats.invocations = stats.invocations;
		eventStats.hits = stats.hits;
		
		return eventStats;
	}
}
