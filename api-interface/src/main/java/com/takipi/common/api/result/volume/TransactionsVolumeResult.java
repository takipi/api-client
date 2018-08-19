package com.takipi.common.api.result.volume;

import java.util.List;

import com.takipi.common.api.data.volume.Transaction;
import com.takipi.common.api.result.intf.ApiResult;

public class TransactionsVolumeResult implements ApiResult {
	public List<Transaction> transactions;
}
