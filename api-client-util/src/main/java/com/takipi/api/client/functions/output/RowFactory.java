package com.takipi.api.client.functions.output;

public interface RowFactory {
	public SeriesRow read(Series series, int index);
	public Class<? extends SeriesRow> rowType();
	public Class<? extends SeriesHeader> HeaderType();
}
