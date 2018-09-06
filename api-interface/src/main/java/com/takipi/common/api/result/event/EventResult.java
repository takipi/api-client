package com.takipi.common.api.result.event;

import java.util.List;

import com.takipi.common.api.data.event.Stats;
import com.takipi.common.api.data.event.Location;
import com.takipi.common.api.result.intf.ApiResult;

public class EventResult implements ApiResult {
	public String id;
	public String summary;
	public String type;
	public String first_seen;

	public Location error_location;
	public Location entry_point;
	public Location error_origin;

	public String introduced_by;
	public List<String> labels;

	public List<String> similar_event_ids;

	public Stats stats;
}
