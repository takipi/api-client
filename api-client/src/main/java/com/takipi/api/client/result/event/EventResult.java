package com.takipi.api.client.result.event;

import java.util.List;

import com.takipi.api.client.data.event.Location;
import com.takipi.api.client.data.event.Stats;
import com.takipi.api.core.result.intf.ApiResult;

public class EventResult implements ApiResult, Cloneable {
	public String id;
	public String summary;
	public String type;
	public String name;
	public String message;
	public String first_seen;

	public Location error_location;
	public Location entry_point;
	public Location error_origin;

	public String introduced_by;
	public List<String> labels;

	public List<String> similar_event_ids;

	public Stats stats;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (summary != null) {
			sb.append(summary);
		}

		if (id != null) {
			sb.append("(");
			sb.append(id);
			sb.append(")");
		}

		if (sb.length() != 0) {
			return sb.toString();
		}

		return super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (!(obj instanceof EventResult))) {
			return false;
		}

		EventResult other = (EventResult) obj;
		
		return ((this.id != null) && (other.id != null) && (id.equals(other.id)));
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : super.hashCode());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		EventResult result = new EventResult();
		
		result.id = this.id;
		result.summary = this.summary;
		result.type = this.type;
		result.name = this.name;
		result.message = this.message;
		result.first_seen = this.first_seen;
		result.error_location = this.error_location;
		result.entry_point = this.entry_point;
		result.error_origin = this.error_origin;
		result.introduced_by = this.introduced_by;
		result.labels = this.labels;
		result.similar_event_ids = this.similar_event_ids;
		result.stats = this.stats;
		
		return result;
		
	}
}
