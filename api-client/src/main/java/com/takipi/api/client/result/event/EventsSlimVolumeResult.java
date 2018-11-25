package com.takipi.api.client.result.event;

import java.util.List;

import com.takipi.api.core.result.intf.ApiResult;

public class EventsSlimVolumeResult implements ApiResult {
	public List<EventSlimResult> events;
}
