package com.takipi.api.client.util.transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.data.transaction.TransactionGraph.GraphPoint;
import com.takipi.api.client.request.transaction.TransactionsGraphRequest;
import com.takipi.api.client.request.transaction.TransactionsVolumeRequest;
import com.takipi.api.client.result.transaction.TransactionsGraphResult;
import com.takipi.api.client.result.transaction.TransactionsVolumeResult;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.MathUtil;

public class TransactionUtil {

	public static Map<String, Transaction> getTransactions(ApiClient apiClient, String serviceId, String viewId,
			DateTime to, int timespanMinutes) {

		DateTime from = to.minusHours(timespanMinutes);

		return getTransactions(apiClient, serviceId, viewId, to, from);
	}

	public static Map<String, Transaction> getTransactions(ApiClient apiClient, String serviceId, String viewId,
			DateTime from, DateTime to) {

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

		TransactionsVolumeRequest transactionsRequest = TransactionsVolumeRequest.newBuilder().setServiceId(serviceId)
				.setViewId(viewId).setFrom(from.toString(fmt)).setTo(to.toString(fmt)).build();

		Response<TransactionsVolumeResult> transactionsResponse = apiClient.get(transactionsRequest);

		if (transactionsResponse.isBadResponse()) {
			throw new IllegalStateException("Failed getting view transactions.");
		}

		TransactionsVolumeResult transactionsResult = transactionsResponse.data;

		return getTransactionsMap(transactionsResult.transactions);
	}

	public static Map<String, Transaction> getTransactionsMap(Collection<Transaction> transactions) {
		if (CollectionUtil.safeIsEmpty(transactions)) {
			return Collections.emptyMap();
		}

		Map<String, Transaction> result = Maps.newHashMapWithExpectedSize(transactions.size());

		for (Transaction transaction : transactions) {
			if (!Strings.isNullOrEmpty(transaction.name)) {
				result.put(transaction.name, transaction);
			}
		}

		return result;
	}

	public static Map<String, TransactionGraph> getTransactionGraphs(ApiClient apiClient, String serviceId,
			String viewId, DateTime to, int timespanMinutes, int pointsWanted) {

		DateTime from = to.minusHours(timespanMinutes);

		return getTransactionGraphs(apiClient, serviceId, viewId, to, from, pointsWanted);
	}

	public static Map<String, TransactionGraph> getTransactionGraphs(ApiClient apiClient, String serviceId,
			String viewId, DateTime from, DateTime to, int pointsWanted) {

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

		TransactionsGraphRequest transactionsGraphRequest = TransactionsGraphRequest.newBuilder()
				.setServiceId(serviceId).setViewId(viewId).setFrom(from.toString(fmt)).setTo(to.toString(fmt))
				.setWantedPointCount(pointsWanted).build();

		Response<TransactionsGraphResult> transactionsGraphResponse = apiClient.get(transactionsGraphRequest);

		if (transactionsGraphResponse.isBadResponse()) {
			throw new IllegalStateException("Failed getting view transaction graphs.");
		}

		TransactionsGraphResult transactionsGraphResult = transactionsGraphResponse.data;

		return getTransactionGraphsMap(transactionsGraphResult.graphs);
	}

	public static Map<String, TransactionGraph> getTransactionGraphsMap(
			Collection<TransactionGraph> transactionGraphs) {
		if (CollectionUtil.safeIsEmpty(transactionGraphs)) {
			return Collections.emptyMap();
		}

		Map<String, TransactionGraph> result = Maps.newHashMapWithExpectedSize(transactionGraphs.size());

		for (TransactionGraph transactionGraph : transactionGraphs) {
			if (!Strings.isNullOrEmpty(transactionGraph.name)) {
				result.put(transactionGraph.name, transactionGraph);
			}
		}

		return result;
	}

	public static Stats aggregateGraph(TransactionGraph graph) {
		if ((graph == null) || (CollectionUtil.safeIsEmpty(graph.points))) {
			return new Stats();
		}

		double[] invocationsArr = new double[graph.points.size()];
		double[] totalTimeArr = new double[graph.points.size()];
		double[] avgTimeArr = new double[graph.points.size()];

		for (int i = 0; i < graph.points.size(); i++) {
			GraphPoint p = graph.points.get(i);

			if (p.stats == null) {
				continue;
			}

			invocationsArr[i] = p.stats.invocations;
			totalTimeArr[i] = p.stats.total_time;
			avgTimeArr[i] = p.stats.avg_time;
		}

		Stats result = new Stats();

		result.invocations = (long) MathUtil.sum(invocationsArr);
		result.total_time = MathUtil.sum(totalTimeArr);
		result.avg_time = MathUtil.weightedAvg(avgTimeArr, invocationsArr);
		result.avg_time_std_deviation = MathUtil.wightedStdDev(avgTimeArr, invocationsArr);

		return result;
	}
}
