package com.takipi.api.client.functions.output;

import java.util.List;

/**
 * The output returned by the execution of a function. This can be used to describe
 * a variable, graph, table, single stat,..
 */
public class Series {
	
	public static final String SUM_COLUMN = "sum";
	public static final String TIME_COLUMN = "time";
	
	/**
	 * Series name
	 */
	public String name;
	
	/**
	 * For functions returning grouped results, the group keys
	 */
	public List<String> tags;
	
	/**
	 * A list of column names returned by the function
	 */
	public List<String> columns;
	
	/**
	 * A list of arrays containing the results of the function
	 */
	public List<List<Object>> values;
	
	public Object getValue(String column, int index) {
		
		if (columns == null) {
			throw new IllegalStateException("columns null");
		}
		
		if (values == null) {
			throw new IllegalStateException("colIndex null");
		}
				
		if ((index < 0) || (index > values.size())) {
			throw new IllegalArgumentException("Bad row index " + String.valueOf(index) + " for " + values.size() + " rows");
		}
		
		int colIndex= columns.indexOf(column);

		if (colIndex == -1) {
			throw new IllegalArgumentException(column + " not found in " + String.join(",", columns));
		}
		
		List<Object> row = values.get(index);
		
		if (colIndex > values.size()) {
			throw new IllegalArgumentException("Bad column index " +
				String.valueOf(colIndex) + " for row " + index + " with " + row.size());
		}
	
		Object result = row.get(colIndex);
		
		return result;
	}
	
	public boolean isSingleStat() {
		
		if (values == null) {
			return false;
		}
		
		if (columns == null) {
			return false;
		}
		
		if (values.size() != 1) {
			return false;
		}
		
		if (!columns.contains(SUM_COLUMN)) {
			return false;
		}
		
		if (!columns.contains(TIME_COLUMN)) {
			return false;
		}
		
		return true;
	}
	
	public Object getSingleStat() {
		
		if (!isSingleStat()) {
			throw new IllegalStateException("Not a single stat series");
		}
		
		return getValue(SUM_COLUMN, 0);
	}
	
}