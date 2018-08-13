package com.takipi.common.api.result.volume;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class TransactionsVolumeResult implements ApiResult {
	public List<Transaction> transactions;

	public static class Transaction {
		public String name;
		public Stats stats;
	}

	public static class Stats {
		public long hits;
		public long invocations;
	}
}
