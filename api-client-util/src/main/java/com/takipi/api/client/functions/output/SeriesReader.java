package com.takipi.api.client.functions.output;

public interface SeriesReader<T extends SeriesRow> {
	public T read(Series<T> series, int index);
	public Class<? extends SeriesRow> rowType();
	public Class<? extends SeriesHeader> headerType();
}
