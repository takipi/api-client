package com.takipi.api.client.result.event;

import java.util.List;

import com.takipi.api.core.result.intf.ApiResult;

public class EventsVolumeResult implements ApiResult {
	public List<EventResult> events;
}
