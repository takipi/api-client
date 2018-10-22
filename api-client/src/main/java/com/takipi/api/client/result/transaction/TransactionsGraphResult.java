package com.takipi.api.client.result.transaction;

import java.util.List;

import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.core.result.intf.ApiResult;

public class TransactionsGraphResult implements ApiResult {
	public List<TransactionGraph> graphs;
}
