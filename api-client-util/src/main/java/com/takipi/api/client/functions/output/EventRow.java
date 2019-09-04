package com.takipi.api.client.functions.output;

import com.takipi.api.client.functions.input.EventsInput;

public class EventRow implements SeriesRow {
	
	public static class Factory implements RowFactory {

		@Override
		public SeriesRow read(Series series, int index) {
			return new EventRow(series, index);
		}

		@Override
		public Class<?> rowType() {
			return EventRow.class;
		}	
	}
	
	public String id;
	public String env_id;
	public String link;
	
	public String summary;
	public String type;
	public String name;
	
	public String typeMessage;
	public String message;
	public String description;	

	public double first_seen;
	public String introduced_by;
	public double last_seen;

	public String error_location;
	public String entry_point;
	public String error_origin;
	public String entry_point_name;
	
	public String labels;
	public String similar_event_ids;
	public boolean is_rethrow;
	
	public long hits;
	public long invocations;
	
	public String jira_issue_url;	
	public String jira_state;	
	public double rate;	
	public String rate_desc;
	
	public String rate_delta;
	public String rate_delta_desc;
	
	public int rank;
	
	public EventRow(Series series, int index) {
					
		this.env_id = series.getString(EventsInput.ENV_ID, index);
		this.id = series.getString(EventsInput.ID, index);
		
		this.summary = series.getString(EventsInput.SUMMARY, index);
		this.message = series.getString(EventsInput.MESSAGE, index);
		this.type = series.getString(EventsInput.TYPE, index);
		this.typeMessage = series.getString(EventsInput.TYPE_MESSAGE, index);
		this.description = series.getString(EventsInput.DESCRIPTION, index);

		this.first_seen = series.getDouble(EventsInput.FIRST_SEEN, index);
		this.introduced_by = series.getString(EventsInput.INTRODUCED_BY, index);
		this.last_seen = series.getDouble(EventsInput.LAST_SEEN, index);

		this.error_location = series.getString(EventsInput.ERROR_LOCATION, index);
		this.error_origin = series.getString(EventsInput.ERROR_ORIGIN, index);
		this.entry_point_name = series.getString(EventsInput.ENTRY_POINT_NAME, index);
		
		this.hits = series.getLong(EventsInput.HITS, index);
		this.invocations = series.getLong(EventsInput.INVOCATIONS, index);
		
		this.labels =  series.getString(EventsInput.LABELS, index);
		this.link = series.getString(EventsInput.LINK, index);

		this.similar_event_ids = series.getString(EventsInput.SIMILAR_EVENT_UDS, index);
		this.is_rethrow = series.getBoolean(EventsInput.IS_RETHROW, index);
				
		this.jira_state = series.getString(EventsInput.JIRA_STATE, index);
		this.jira_issue_url = series.getString(EventsInput.JIRA_ISSUE_URL, index);		
		
		this.rate = series.getDouble(EventsInput.RATE, index);
		this.rate_desc = series.getString(EventsInput.RATE_DESC, index);
		this.rate_delta = series.getString(EventsInput.RATE_DELTA, index);
		this.rate_delta_desc = series.getString(EventsInput.RATE_DELTA_DESC, index);
		
		this.rank = series.getInt(EventsInput.RANK, index);		
	}
}
