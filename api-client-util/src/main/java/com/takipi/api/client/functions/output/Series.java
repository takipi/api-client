package com.takipi.api.client.functions.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.takipi.api.client.functions.input.BaseGraphInput;
import com.takipi.api.client.functions.input.EventsInput;
import com.takipi.api.client.functions.input.RegressionsInput;
import com.takipi.api.client.functions.input.ReliabilityReportInput;
import com.takipi.api.client.functions.input.TransactionsListInput;

/**
 * The output returned by the execution of a function. This can be used to describe
 * a variable, graph, table, single stat,..
 */
public class Series  {
	
	public static final String SUM_COLUMN = "sum";
	public static final String TIME_COLUMN = "time";
	
	private static final Gson gson = new Gson();
	private static Map<String, RowFactory> factories;
	
	/**
	 * Series name
	 */
	public String name;
	
	/**
	 * Series type - events, regressions, graph,..
	 */
	public String type;
	
	/**
	 * Series header - metadata about this series
	 */
	public String header;
	
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
		return getValue(column, index, false);
	}
	
	public Object getValue(int colIndex, int index) {
	
		if (values == null) {
			throw new IllegalStateException("colIndex null");
		}
				
		if ((index < 0) || (index > values.size())) {
			throw new IllegalArgumentException("Bad row index " + String.valueOf(index) + " for " + values.size() + " rows");
		}
		
		List<Object> row = values.get(index);
		
		if (colIndex >row.size()) {
			throw new IllegalArgumentException("Bad column index " +
				String.valueOf(colIndex) + " for row " + index + " with " + row.size());
		}
		
		Object result = row.get(colIndex);
		
		return result;
	}

	
	public Object getValue(String column, int index, boolean mustExist) {
		
		if (columns == null) {
			throw new IllegalStateException("columns null");
		}
		
		int colIndex= columns.indexOf(column);

		if (colIndex == -1) {
			
			if (!mustExist) {
				return null;
			}
			
			throw new IllegalArgumentException(column + " not found in " + String.join(",", columns));
		}
		
		return getValue(colIndex, index);
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
	
	public long getLong(String column, int index) {
		
		Object value = getValue(column, index);
		
		if (value == null) {
			return 0;
		}
		
		if (value instanceof Long) {
			return (Long)value;
		}
		
		if (value instanceof Integer) {
			return (Long)value;
		}
		
		if (value instanceof Double) {
			return ((Double)value).intValue();
		}

		if (value instanceof String) {
			
			try {
				return Long.valueOf(value.toString());
			} catch (Exception e) {
				return 0;
			}
		}
		
		return 0;
	}
	
	public int getInt(String column, int index) {	
		return (int)getLong(column, index);
	}
	
	public String getString(String column, int index) {
	
		Object value = getValue(column, index);
		
		if (value == null) {
			return null;
		}
		
		return value.toString();
	}
	
	public double getDouble(String column, int index) {
		
		Object value = getValue(column, index);
		
		if (value == null) {
			return 0;
		}
		
		if (value instanceof Long) {
			return (Long)value;
		}
		
		if (value instanceof Integer) {
			return (Long)value;
		}
		
		if (value instanceof Double) {
			return ((Double)value);
		}

		if (value instanceof String) {
			
			try {
				return Long.valueOf(value.toString());
			} catch (Exception e) {
				return 0;
			}
		}
		
		return 0;
	}
	
	public boolean getBoolean(String column, int index) {
		
		Object value = getValue(column, index);
		
		if (value == null) {
			return false;
		}
		
		if (value instanceof String) {
			
			try {
				return Boolean.valueOf(value.toString());
			} catch (Exception e) {
				return false;
			}
		}
		
		return false;
	}
	
	public SeriesRow readRow(int index) {
		
		if (type == null) {
			return null;
		}
		
		RowFactory factory = factories.get(type);
		
		if (factory == null) {
			return null;
		}
		
		return factory.read(this, index);
	}
	
	public SeriesHeader getHeader() {
		
		if (type == null) {
			return null;
		}
		
		if (header == null) {
			return null;
		}
		
		RowFactory factory = factories.get(type);
		
		if (factory == null) {
			return null;
		} 
		
		Class<?> headerClass = factory.HeaderType();
		
		return (SeriesHeader)gson.fromJson(header, headerClass);
	}
	
	public Class<?> getRowType() {
		
		if (type == null) {
			return null;
		}
		
		RowFactory factory = factories.get(type);
		
		if (factory == null) {
			return null;
		} 
		
		return factory.rowType();
	}
	
	static {
		
		factories = new HashMap<String, RowFactory>();
		
		factories.put(EventsInput.EVENTS_SERIES, new EventRow.Factory());
		factories.put(TransactionsListInput.TRANSACTION_SERIES, new TransactionRow.Factory());
		factories.put(RegressionsInput.REGRESSIONS_SERIES, new RegressionRow.Factory());
		factories.put(ReliabilityReportInput.RELIABITY_REPORT_SERIES, new ReliabilityReportRow.Factory());
		factories.put(BaseGraphInput.GRAPH_SERIES, new GraphRow.Factory());


	}
}