package com.takipi.common.api.result.volume;

import java.util.List;

import com.takipi.common.api.result.event.EventResult;
import com.takipi.common.api.result.intf.ApiResult;

public class EventsVolumeResult implements ApiResult {
	public List<EventResult> events;
}
