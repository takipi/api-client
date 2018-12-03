package com.takipi.api.client.result.transactiontimer;

import java.util.List;

import com.takipi.api.client.data.timer.Timer;
import com.takipi.api.core.result.intf.ApiResult;

public class TransactionTimersResult implements ApiResult {
	public List<Timer> transaction_timers;
}
