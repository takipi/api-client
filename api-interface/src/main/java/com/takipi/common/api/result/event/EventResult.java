package com.takipi.common.api.result.event;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class EventResult implements ApiResult {
	public String id;
	public String summary;
	public String type;
	public String first_seen;

	public Location error_location;
	public Location entry_point;

	public String introduced_by;
	public List<String> labels;

	public List<String> similar_event_ids;

	public Stats stats;

	public static class Location {
		public String prettified_name;
		public String class_name;
		public String method_name;
		public String method_desc;
	}

	public static class Stats {
		public long hits;
		public long invocations;
	}
}
