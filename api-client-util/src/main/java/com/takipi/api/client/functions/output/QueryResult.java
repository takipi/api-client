package com.takipi.api.client.functions.output;

import java.io.PrintStream;
import java.util.List;

import com.google.gson.Gson;
import com.takipi.api.core.result.intf.ApiResult;

public class QueryResult implements ApiResult {
	
	private static final Gson gson = new Gson();

	
	public List<ResultContent> results;
	
	public ResultContent getResult() {
		
		if (results == null) {
			throw new IllegalStateException("results null");
		}
		
		if (results.size() == 0) {
			throw new IllegalStateException("results empty");	
		}
		
		return results.get(0);	
	}
	
	public void print(PrintStream stream) {
		stream.println(gson.toJson(this));
	}
}
