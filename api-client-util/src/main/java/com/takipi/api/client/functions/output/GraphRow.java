package com.takipi.api.client.functions.output;

import org.joda.time.DateTime;

public class GraphRow implements SeriesRow  {
	
	public static class Factory implements RowFactory {

		@Override
		public SeriesRow read(Series series, int index) {
			return new GraphRow(series, index);
		}

		@Override
		public Class<? extends SeriesRow> rowType() {
			return GraphRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> HeaderType() {
			return null;
		}
	}
	
	@Override 
	public String toString() {
		return time + " + " + value;
	}
	
	public DateTime time;
	public double value;

	public GraphRow(Series series, int index) {
		
		Double time = (Double)series.getValue(0, index);
		Double value = (Double)series.getValue(1, index);

		this.time = new DateTime(time.longValue());
		this.value = value.doubleValue();
	}
}
