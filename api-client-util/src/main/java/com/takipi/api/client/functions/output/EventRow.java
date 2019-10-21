package com.takipi.api.client.functions.output;

public class EventRow extends BaseEventRow implements SeriesRow {
	
	public static class Reader implements SeriesReader<EventRow> {

		@Override
		public EventRow read(Series<EventRow> series, int index) {
			return new EventRow(series, index);
		}

		@Override
		public Class<? extends SeriesRow> rowType() {
			return EventRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return null;
		}	
	}
	
	public EventRow(Series<?> series, int index) {
		super(series, index);
	}
}
