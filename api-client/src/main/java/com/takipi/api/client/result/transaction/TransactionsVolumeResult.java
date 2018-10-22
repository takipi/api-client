package com.takipi.api.client.result.transaction;

import java.util.List;

import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.core.result.intf.ApiResult;

public class TransactionsVolumeResult implements ApiResult {
	public List<Transaction> transactions;
}
