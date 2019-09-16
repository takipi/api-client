package com.takipi.api.client.functions.output;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.takipi.api.client.functions.input.BaseGraphInput;
import com.takipi.api.client.functions.input.EventsInput;
import com.takipi.api.client.functions.input.RegressionsInput;
import com.takipi.api.client.functions.input.ReliabilityReportInput;
import com.takipi.api.client.functions.input.TransactionsListInput;
import com.takipi.common.util.CollectionUtil;

/**
 * The output returned by the execution of a function. This can be used to describe
 * a variable, graph, table, single stat,..
 */
public class Series  implements Iterable<SeriesRow> {
	
	public static final String SUM_COLUMN = "sum";
	public static final String TIME_COLUMN = "time";
	
	private static final Gson gson = new Gson();
	private static Map<String, SeriesReader> seriesReaders;
	
	/**
	 * Series name
	 */
	public String name;
	
	/**
	 * Series type - volume, events, regressions, graph, relability_report,...
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

	private int getColumnIndex(String column, boolean mustExist) {
		
		if (columns == null) {
			throw new IllegalStateException("columns null");
		}
		
		int result = columns.indexOf(column);

		if (result == -1) {
			
			if (!mustExist) {
				return -1;
			}
			
			throw new IllegalArgumentException(column + " not found in " + String.join(",", columns));
		}
		
		return result;
	}
	
	public Object getValue(String column, int index, boolean mustExist) {
		
		int colIndex = getColumnIndex(column, mustExist);
		
		if (colIndex == -1) {
			return null;
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
			return (Integer)value;
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
	
	protected double parseDouble(Object value) {
		
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
	
	public double getDouble(String column, int index) {
		
		Object value = getValue(column, index);
		return parseDouble(value);
		
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
	
	public void sort(String columnName, boolean ascending, boolean numerically) {
		
		if (numerically) {
			sortNumerically(columnName, ascending);
		} else {
			sortComparabales(columnName, ascending);
		}
	}

	private void sortNumerically(String columnName, boolean ascending) {
		
		if (CollectionUtil.safeIsEmpty(values)) {
			return;
		}
		
		int colIndex = getColumnIndex(columnName, true);
		
		values.sort(new Comparator<List<Object>>() {

			@Override
			public int compare(List<Object> o1, List<Object> o2) {
				
				Object v1 = o1.get(colIndex);
				Object v2 = o2.get(colIndex);
				
				double c1;
				double c2;
				
				if (ascending) {
					
					c1 = parseDouble(v1);
					c2 = parseDouble(v2);
				} else {
					
					c1 = parseDouble(v2);
					c2 = parseDouble(v1);
				}
				
				return Double.compare(c1, c2);
			}
		});
	}
	
	private void sortComparabales(String columnName, boolean ascending) {
		
		if (CollectionUtil.safeIsEmpty(values)) {
			return;
		}
		
		int colIndex = getColumnIndex(columnName, true);
		
		values.sort(new Comparator<List<Object>>() {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(List<Object> o1, List<Object> o2) {
				
				Object v1 = o1.get(colIndex);
				Object v2 = o2.get(colIndex);
				
				Comparable<Object> c1;
				Object c2;
				
				if (ascending) {
					
					if (v1 instanceof Comparable<?>) {
						c1 = (Comparable<Object>)v1;
					} else {
						c1 = null;
					}
					c2 = v2;
				} else {
					
					if (v2 instanceof Comparable<?>) {
						c1 = (Comparable<Object>)v2;
					} else {
						c1 = null;
					}
					c2 = v1;
				}
				
				if (c1 == null) {
					return 0;
				}
				
				return c1.compareTo(c2);
			}
		});
	}
	
	public List<SeriesRow> readRows() {
				
		int size = this.size();
		
		List<SeriesRow> result = new ArrayList<SeriesRow>(size);
		
		if (size == 0) {
			return result;
		}
		
		for (SeriesRow row : this) {
			result.add(row);
		}
		
		return result;
	}
	
	public SeriesRow readRow(int index) {
		
		if (type == null) {
			return null;
		}
		
		SeriesReader reader = seriesReaders.get(type);
		
		if (reader == null) {
			return null;
		}
		
		return reader.read(this, index);
	}
	
	public SeriesHeader getHeader() {
		
		if (type == null) {
			return null;
		}
		
		if (header == null) {
			return null;
		}
		
		SeriesReader reader = seriesReaders.get(type);
		
		if (reader == null) {
			return null;
		} 
		
		Class<?> headerClass = reader.headerType();
		
		if (headerClass == null) {
			return null;
		} 
		
		return (SeriesHeader)gson.fromJson(header, headerClass);
	}
	
	public Class<?> getRowType() {
		
		if (type == null) {
			return null;
		}
		
		SeriesReader reader = seriesReaders.get(type);
		
		if (reader == null) {
			return null;
		} 
		
		return reader.rowType();
	}
	
	@Override
	public Iterator<SeriesRow> iterator() {
		
		return new Iterator<SeriesRow>() {

			private int index;
			
			@Override
			public boolean hasNext() {
				return index < values.size();
			}

			@Override
			public SeriesRow next() {
				return readRow(index++);
			}
		};
	}
	
	public int size() {
		
		if (CollectionUtil.safeIsEmpty(values)) {
			return 0;
		}
		
		return values.size();
		
	}
	
	static {
		
		seriesReaders = new HashMap<String, SeriesReader>();
		
		seriesReaders.put(EventsInput.EVENTS_SERIES, new EventRow.Reader());
		seriesReaders.put(BaseGraphInput.GRAPH_SERIES, new GraphRow.Reader());

		seriesReaders.put(TransactionsListInput.TRANSACTION_SERIES, new TransactionRow.Reader());
		seriesReaders.put(RegressionsInput.REGRESSIONS_SERIES, new RegressionRow.Reader());
		
		seriesReaders.put(ReliabilityReportInput.RELIABITY_REPORT_SERIES, new ReliabilityReportRow.Reader());
		seriesReaders.put(ReliabilityReportInput.REGRESSION_SERIES, new ReliabilityReportRow.RegressionReader());
		seriesReaders.put(ReliabilityReportInput.ERRORS_SERIES, new ReliabilityReportRow.EventVolumeReader());
		seriesReaders.put(ReliabilityReportInput.FAILURES_SERIES, new ReliabilityReportRow.EventVolumeReader());
		seriesReaders.put(ReliabilityReportInput.SLOWDOWN_SERIES, new ReliabilityReportRow.SlowdownReader());
	}
}