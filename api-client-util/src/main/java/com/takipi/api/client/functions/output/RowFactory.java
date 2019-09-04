package com.takipi.api.client.functions.output;

public interface RowFactory {
	public SeriesRow read(Series series, int index);
	public Class<?> rowType();
}
