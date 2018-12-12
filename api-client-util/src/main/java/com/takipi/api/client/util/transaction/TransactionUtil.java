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
import com.takipi.api.client.data.transaction.Transaction;
import com.takipi.api.client.request.transaction.TransactionsVolumeRequest;
import com.takipi.api.client.result.transaction.TransactionsVolumeResult;
import com.takipi.api.core.url.UrlClient.Response;
import com.takipi.common.util.CollectionUtil;

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
}
