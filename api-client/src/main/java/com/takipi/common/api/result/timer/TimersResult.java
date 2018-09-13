package com.takipi.common.api.result.timer;

import java.util.List;

import com.takipi.common.api.data.timer.Timer;
import com.takipi.common.api.result.intf.ApiResult;

public class TimersResult implements ApiResult {
	public List<Timer> timers;
}
