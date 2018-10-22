package com.takipi.api.client.result.timer;

import java.util.List;

import com.takipi.api.client.data.timer.Timer;
import com.takipi.api.core.result.intf.ApiResult;

public class TimersResult implements ApiResult {
	public List<Timer> timers;
}
