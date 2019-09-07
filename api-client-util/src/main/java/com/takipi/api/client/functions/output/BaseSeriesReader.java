package com.takipi.api.client.functions.output;

public abstract class BaseSeriesReader implements SeriesReader  {
	
	@Override
	public Class<? extends SeriesHeader> headerType() {
		return null;
	}
}
