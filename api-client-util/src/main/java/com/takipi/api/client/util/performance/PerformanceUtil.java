package com.takipi.api.client.util.performance;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.request.event.EventModifyLabelsRequest;
import com.takipi.api.client.request.label.CreateLabelRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.performance.calc.PerformanceCalculator;
import com.takipi.api.client.util.performance.calc.PerformanceScore;
import com.takipi.api.client.util.performance.calc.PerformanceState;
import com.takipi.api.client.util.transaction.TransactionUtil;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class PerformanceUtil {

	private static final String PERF_SUFFIX = ".perf";

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

	public static Pair<Collection<String>, Collection<String>> categorizeEvent(EventResult event, String serviceId,
			PerformanceState state, Set<String> existingLabels, ApiClient apiClient, boolean applyLabels) {
		if (event == null) {
			return Pair.of(Collections.emptySet(), Collections.emptySet());
		}

		Set<String> labelsToRemove = Sets.newHashSet();

		if (!CollectionUtil.safeIsEmpty(event.labels)) {
			for (String currentLabel : event.labels) {
				if (currentLabel.endsWith(PERF_SUFFIX)) {
					labelsToRemove.add(currentLabel);
				}
			}
		}

		Set<String> labelsToAdd;

		if (state != PerformanceState.NO_DATA) {
			labelsToAdd = Sets.newHashSetWithExpectedSize(1);

			String labelName = getLabelName(state);

			if (!Strings.isNullOrEmpty(labelName)) {
				String perfLabelName = toInternalPerfLabelName(labelName);
				labelsToRemove.remove(perfLabelName);

				if (!CollectionUtil.safeContains(event.labels, perfLabelName)) {
					labelsToAdd.add(perfLabelName);

					if (existingLabels.add(labelName)) {
						createPerfLabel(labelName, serviceId, apiClient);
					}
				}
			}
		} else {
			labelsToAdd = Collections.emptySet();
		}

		if (!applyLabels) {
			return Pair.of(labelsToAdd, labelsToRemove);
		}

		if ((!labelsToAdd.isEmpty()) || (!labelsToRemove.isEmpty())) {
			EventModifyLabelsRequest labelsRequest = EventModifyLabelsRequest.newBuilder().setServiceId(serviceId)
					.setEventId(event.id).addLabels(labelsToAdd).removeLabels(labelsToRemove).build();

			Response<EmptyResult> addResult = apiClient.post(labelsRequest);

			if (addResult.isBadResponse()) {
				throw new IllegalStateException("Can't apply labels to event " + event.id);
			}
		}

		return Pair.of(labelsToAdd, labelsToRemove);
	}

	private static String getLabelName(PerformanceState state) {
		switch (state) {
		case NO_DATA:
			return null;
		case OK:
			return "OK";
		case SLOWING:
			return "Slowing";
		case CRITICAL:
			return "Critical";
		}

		return null;
	}

	// Returns true if the label already existed.
	//
	private static boolean createPerfLabel(String labelName, String serviceId, ApiClient apiClient) {
		String infraLabelName = toInternalPerfLabelName(labelName);

		CreateLabelRequest createLabelRequest = CreateLabelRequest.newBuilder().setServiceId(serviceId)
				.setName(infraLabelName).build();

		Response<EmptyResult> createResponse = apiClient.post(createLabelRequest);

		if ((createResponse.isBadResponse()) && (createResponse.responseCode != HttpURLConnection.HTTP_CONFLICT)) {
			throw new IllegalStateException("Can't create label " + infraLabelName);
		}

		return (createResponse.responseCode == HttpURLConnection.HTTP_CONFLICT);
	}

	private static String toInternalPerfLabelName(String labelName) {
		return labelName + PERF_SUFFIX;
	}
}
