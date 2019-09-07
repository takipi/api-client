package com.takipi.api.client.functions.output;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.takipi.api.core.result.intf.ApiResult;
import com.takipi.common.util.CollectionUtil;

public class QueryResult implements ApiResult {
	
	private static final Gson gson = new Gson();

	public List<ResultContent> results;
	
	public void print(PrintStream stream) {
		stream.println(gson.toJson(this));
	}
	
	public Collection<Series> getSeries() {
		
		if (CollectionUtil.safeIsEmpty(results)) {
			return Collections.emptyList();
		}
		
		List<Series> result = new ArrayList<Series>();
		
		for (ResultContent resultContent : results) {
			
			if (CollectionUtil.safeIsEmpty(resultContent.series)) {
				continue;
			}
			
			result.addAll(resultContent.series);
		}
		
		return result;
	}
}
