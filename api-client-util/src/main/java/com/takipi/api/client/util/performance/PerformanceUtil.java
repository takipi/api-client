package com.takipi.api.client.util.performance;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.util.performance.calc.PerformanceCalculator;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.transaction.TransactionUtil;
import com.takipi.common.util.CollectionUtil;

public class PerformanceUtil {

	public static Map<Transaction, PerformanceScore> getTransactionStates(Collection<Transaction> activeTransactions,
			Collection<Transaction> baselineTransactions, PerformanceCalculator<Transaction> calculator) {

		return getTransactionStates(TransactionUtil.getTransactionsMap(activeTransactions),
				TransactionUtil.getTransactionsMap(baselineTransactions), calculator);
	}

	public static Map<Transaction, PerformanceScore> getTransactionStates(ApiClient apiClient, String serviceId,
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

	public static Map<Transaction, PerformanceScore> getTransactionStates(Map<String, Transaction> activeTransactions,
			Map<String, Transaction> baselineTransactions, PerformanceCalculator<Transaction> calculator) {
		return getPerformanceStates(activeTransactions, baselineTransactions, calculator);
	}

	public static <T> Map<T, PerformanceScore> getPerformanceStates(Map<String, T> activeTargets,
			Map<String, T> baselineTargets, PerformanceCalculator<T> calculator) {

		if (CollectionUtil.safeIsEmpty(activeTargets)) {
			return Collections.emptyMap();
		}

		Map<T, PerformanceScore> result = Maps.newHashMapWithExpectedSize(activeTargets.size());

		if (CollectionUtil.safeIsEmpty(baselineTargets)) {
			for (Entry<String, T> entry : activeTargets.entrySet()) {
				result.put(entry.getValue(), PerformanceScore.NO_DATA);
			}

			return result;
		}

		for (Entry<String, T> entry : activeTargets.entrySet()) {
			String name = entry.getKey();
			T active = entry.getValue();
			T baseline = baselineTargets.get(name);

			if (baseline == null) {
				result.put(active, PerformanceScore.NO_DATA);
				continue;
			}

			PerformanceScore score = calculator.calc(active, baseline);

			result.put(active, score);
		}

		return result;
	}
}
