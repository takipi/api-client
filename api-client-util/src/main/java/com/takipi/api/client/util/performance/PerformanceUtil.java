package com.takipi.api.client.util.performance;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.util.performance.compare.PerformanceCalculator;
import com.takipi.api.client.util.transaction.TransactionUtil;
import com.takipi.common.util.CollectionUtil;

public class PerformanceUtil {

	public static Map<Transaction, PerformanceState> getTransactionStates(Collection<Transaction> activeTransactions,
			Collection<Transaction> baselineTransactions, PerformanceCalculator<Transaction> calculator) {

		return getTransactionStates(TransactionUtil.getTransactionsMap(activeTransactions),
				TransactionUtil.getTransactionsMap(baselineTransactions), calculator);
	}

	public static Map<Transaction, PerformanceState> getTransactionStates(ApiClient apiClient, String serviceId,
			String viewId, PerformanceCalculator<Transaction> calculator, int baselineTimespanMinutes,
			int activeTimespanMinutes) {

		DateTime now = DateTime.now();

		Map<String, Transaction> activeTransactions = TransactionUtil.getTransactions(apiClient, serviceId, viewId, now,
				activeTimespanMinutes);

		if (CollectionUtil.safeIsEmpty(activeTransactions)) {
			return Collections.emptyMap();
		}

		Map<String, Transaction> baselineTransactions = TransactionUtil.getTransactions(apiClient, serviceId, viewId,
				now, baselineTimespanMinutes);

		return getTransactionStates(activeTransactions, baselineTransactions, calculator);
	}

	public static Map<Transaction, PerformanceState> getTransactionStates(Map<String, Transaction> activeTransactions,
			Map<String, Transaction> baselineTransactions, PerformanceCalculator<Transaction> calculator) {
		return getPerformanceStates(activeTransactions, baselineTransactions, calculator);
	}
	
	public static <T> Map<T, PerformanceState> getPerformanceStates(Map<String, T> activeTransactions,
			Map<String, T> baselineTransactions, PerformanceCalculator<T> calculator) {

		if (CollectionUtil.safeIsEmpty(activeTransactions)) {
			return Collections.emptyMap();
		}

		Map<T, PerformanceState> result = Maps.newHashMapWithExpectedSize(activeTransactions.size());

		if (CollectionUtil.safeIsEmpty(baselineTransactions)) {
			for (Entry<String, T> entry : activeTransactions.entrySet()) {
				result.put(entry.getValue(), PerformanceState.NO_DATA);
			}

			return result;
		}

		for (Entry<String, T> entry : activeTransactions.entrySet()) {
			String transactionName = entry.getKey();
			T activeTransaction = entry.getValue();
			T baselineTransaction = baselineTransactions.get(transactionName);

			if (baselineTransaction == null) {
				result.put(activeTransaction, PerformanceState.NO_DATA);
				continue;
			}

			PerformanceState performanceState = calculator.calc(activeTransaction, baselineTransaction);

			result.put(activeTransaction, performanceState);
		}

		return result;
	}
}
