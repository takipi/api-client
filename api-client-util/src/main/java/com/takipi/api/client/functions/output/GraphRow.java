package com.takipi.api.client.functions.output;

import org.joda.time.DateTime;

public class GraphRow implements SeriesRow  {
	
	public static class Reader implements SeriesReader<GraphRow> {

		@Override
		public GraphRow read(Series<GraphRow> series, int index) {
			return new GraphRow(series, index);
		}

		@Override
		public Class<? extends SeriesRow> rowType() {
			return GraphRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return null;
		}
	}
	
	@Override 
	public String toString() {
		return time + " + " + value;
	}
	
	public DateTime time;
	public double value;

	public GraphRow(Series<GraphRow> series, int index) {
		
		Double time = (Double)series.getValue(0, index);
		Double value = (Double)series.getValue(1, index);

		this.time = new DateTime(time.longValue());
		this.value = value.doubleValue();
	}
}
