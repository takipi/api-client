package com.takipi.common.api.result.event;

import java.util.List;

import com.takipi.common.api.data.event.Action;
import com.takipi.common.api.result.intf.ApiResult;

public class EventActionsResult implements ApiResult {
	public List<Action> event_actions;
}
