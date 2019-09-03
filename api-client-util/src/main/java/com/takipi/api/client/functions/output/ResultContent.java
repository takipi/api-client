package com.takipi.api.client.functions.output;

import java.util.List;

import com.google.common.base.Objects;

public class ResultContent {
	public int statement_id;
	public List<Series> series;
	
	public Series getSeries(String name) {
		
		if (series == null) {
			throw new IllegalStateException("series null");
		}
		
		for (Series item : series) {
			
			if (Objects.equal(item.name, name)) {
				return item;
			}
		}
		
		return null;
		
	}
}
