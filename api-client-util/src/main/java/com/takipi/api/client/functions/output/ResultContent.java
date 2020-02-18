package com.takipi.api.client.functions.output;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.takipi.common.util.ObjectUtil;

public class ResultContent {
	
	public int statement_id;
	public List<Series<SeriesRow>> series;
	
	public Series<?> getSeries(String name) {
		
		if (series == null) {
			return null;
		}
		
		for (Series<?> item : series) {
			
			if (ObjectUtil.equal(item.name, name)) {
				return item;
			}
		}
		
		return null;
	}
	
	public Map<String, Series<?>> getSeriesMap() {
		
		if (series == null) {
			Collections.emptyMap();
		}
		
		Map<String, Series<?>> result = new HashMap<String, Series<?>>(series.size());
		
		for (Series<?> s : series) {
			result.put(s.name, s);
		}
		
		return result;
	}
}
