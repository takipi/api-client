package com.takipi.api.client.result.event;

import com.takipi.api.client.data.event.Stats;
import com.takipi.api.core.result.intf.ApiResult;

public class EventSlimResult implements ApiResult, Cloneable {
	public String id;

	public Stats stats;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

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

		if ((obj == null) || (!(obj instanceof EventSlimResult))) {
			return false;
		}

		EventSlimResult other = (EventSlimResult) obj;

		return ((this.id != null) && (other.id != null) && (id.equals(other.id)));
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : super.hashCode());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		EventSlimResult result = new EventSlimResult();

		result.id = this.id;
		result.stats = this.stats;

		return result;
	}
}
