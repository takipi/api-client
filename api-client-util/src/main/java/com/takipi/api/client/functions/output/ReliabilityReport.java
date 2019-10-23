package com.takipi.api.client.functions.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.functions.input.BaseEventVolumeInput;
import com.takipi.api.client.functions.input.RegressionsInput;
import com.takipi.api.client.functions.input.ReliabilityReportInput;
import com.takipi.api.client.functions.output.ReliabilityReportRow.Header;
import com.takipi.api.core.url.UrlClient.Response;

public class ReliabilityReport {
	
	public Map<ReliabilityReportRow.Header, ReliabilityReportItem> items;
	
	public static class ReliabilityReportItem {
		
		public ReliabilityReportRow row;
		public Series<RegressionRow> regressions;
		public Series<EventRow> errors;
		public Series<EventRow> failures;
		public Series<TransactionRow> transactions;
		
		protected ReliabilityReportItem(ReliabilityReportRow row) {
			this.row = row;
		}
		
		public Collection<RegressionRow> getNewErrors(boolean includeNonSev, boolean includeSev) {
			
			if (regressions == null) {
				return Collections.emptyList();
			}
			
			List<RegressionRow> result = new ArrayList<RegressionRow>(regressions.size());
			
			for (RegressionRow row : regressions) {
				
				if ((includeNonSev) && (row.regression_type.equals(RegressionsInput.NEW_ISSUE_REGRESSIONS))) {
					result.add(row);
				}
				
				if ((includeSev) && (row.regression_type.equals(RegressionsInput.SEVERE_NEW_ISSUE_REGRESSIONS))) {
					result.add(row);
				}
			}
			
			return result;	
		}
		
		public Collection<RegressionRow> geIncErrors(boolean includeNonSev, boolean includeSev) {
			
			if (regressions == null) {
				return Collections.emptyList();
			}
			
			List<RegressionRow> result = new ArrayList<RegressionRow>(regressions.size());
			
			for (RegressionRow row : regressions) {
				
				if ((includeNonSev) && (row.regression_type.equals(RegressionsInput.INC_ERROR_REGRESSIONS))) {
					result.add(row);
				}
				
				if ((includeSev) && (row.regression_type.equals(RegressionsInput.SEVERE_INC_ERROR_REGRESSIONS))) {
					result.add(row);
				}
			}
			
			return result;	
		}
		
		public Collection<TransactionRow> getSlowdowns(boolean includeNonSev, boolean includeSev) {
			
			if (transactions == null) {
				return Collections.emptyList();
			}
			
			List<TransactionRow> result = new ArrayList<TransactionRow>(transactions.size());
			
			for (TransactionRow row : transactions) {
				
				if ((includeNonSev) &&  (row.slow_state == BaseEventVolumeInput.SLOWING_ORDINAL)) {
					result.add(row);
				}
				
				if ((includeSev) &&  (row.slow_state == BaseEventVolumeInput.CRITICAL_ORDINAL)) {
					result.add(row);
				}
			}
			
			return result;	
		}
	}
	
	protected ReliabilityReport() {
		this.items = new HashMap<ReliabilityReportRow.Header, ReliabilityReportItem>();
	}
	
	@SuppressWarnings("unchecked")
	public static ReliabilityReport execute(ApiClient apiClient, ReliabilityReportInput input) {
		
		Response<QueryResult> response = apiClient.get(input);
		
		if (response.isBadResponse()) {
			return null;
		}
		
		ReliabilityReport result = new ReliabilityReport();
				
		for (Series<SeriesRow> series : response.data.getSeries()) {
			
			if (!series.type.equals(ReliabilityReportInput.RELIABITY_REPORT_SERIES)) {
				continue;
			}
			
			for (SeriesRow row : series) {
				ReliabilityReportRow rrRow = (ReliabilityReportRow)row;
				ReliabilityReportItem rrItem = new ReliabilityReportItem(rrRow);
				ReliabilityReportRow.Header rrHeader = new Header(rrRow.serviceId, rrRow.key);
				result.items.put(rrHeader, rrItem);
			}	
		}
		
		for (Series<?> series : response.data.getSeries()) {
			
			SeriesHeader header = series.getHeader();
			
			if (!(header instanceof ReliabilityReportRow.Header)) {
				continue;
			}
			
			ReliabilityReportRow.Header rrHeader = (ReliabilityReportRow.Header)header;
			ReliabilityReportItem rrItem = result.items.get(rrHeader);
			
			if (rrItem == null) {
				continue;
			}
			
			switch (series.type) {
				
				case ReliabilityReportInput.REGRESSION_SERIES: {	
					rrItem.regressions = (Series<RegressionRow>)series;
					break;
				}
	
				case ReliabilityReportInput.FAILURES_SERIES: {
					rrItem.failures = (Series<EventRow>)series;
					break;
				}
				
				case ReliabilityReportInput.ERRORS_SERIES: {
					rrItem.errors = (Series<EventRow>)series;
					break;
				}
				
				case ReliabilityReportInput.SLOWDOWN_SERIES: {	
					rrItem.transactions = (Series<TransactionRow>)series;
					break;
				}
			}
		}
			
		return result;
	}
	
}
