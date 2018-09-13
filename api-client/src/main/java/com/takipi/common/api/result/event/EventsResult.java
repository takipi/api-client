package com.takipi.common.api.result.event;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class EventsResult implements ApiResult {
	public List<EventResult> events;
}
