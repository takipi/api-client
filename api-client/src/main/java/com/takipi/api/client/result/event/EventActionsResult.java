package com.takipi.api.client.result.event;

import java.util.List;

import com.takipi.api.client.data.event.Action;
import com.takipi.api.core.result.intf.ApiResult;

public class EventActionsResult implements ApiResult {
	public List<Action> event_actions;
}
