package com.takipi.api.client.functions.output;

import com.google.common.base.Objects;
import com.takipi.api.client.functions.input.EventsInput;
import com.takipi.api.client.functions.input.ReliabilityReportInput;

public class ReliabilityReportRow implements SeriesRow {
	
	public static class Reader implements SeriesReader<ReliabilityReportRow> {

		@Override
		public ReliabilityReportRow read(Series<ReliabilityReportRow> series, int index) {
			return new ReliabilityReportRow(series, index);
		}	
		
		@Override
		public Class<? extends SeriesRow> rowType() {
			return ReliabilityReportRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return null;
		}	
	}
	
	public static class EventVolumeReader extends EventRow.Reader {

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return Header.class;
		}
	}
	
	public static class SlowdownReader extends TransactionRow.Reader {

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return Header.class;
		}
	}
	
	public static class RegressionReader extends RegressionRow.Reader {

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return Header.class;
		}
	}
	
	public static class Header implements SeriesHeader {
		
		public String serviceId;
		public String key;
		
		public Header(String serviceId, String key) {
			this.serviceId = serviceId;
			this.key = key;
		}
		
		@Override
		public int hashCode() {
			return serviceId.hashCode() ^ key.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if (!(obj instanceof ReliabilityReportRow.Header)) {
				return false;
			}
			
			ReliabilityReportRow.Header other = (ReliabilityReportRow.Header)obj;
			
			if (!Objects.equal(serviceId, other.serviceId)) {
				return false;
			}
			
			if (!Objects.equal(key, other.key)) {
				return false;
			}
			
			return true;
		}
		
		@Override
		public String toString() {
			return key;
		}
	}
	
	/**
	 * Field values documented in matching ReliabilityReportInput constants
	 */
	
	public String service;
	public String serviceId;

	public String key;
	public String name;
	
	public String previousDepName;
	public String previousDepFrom;			
	public String previousDepState;
	public String timelineDiffState;
	
	public String newIssues;
	public String regressions;
	public String slowdowns; 
	
	public String newIssuesDesc;
	public String regressionsDesc;
	public String slowdownsDesc;
	
	public double score; 
	public String scoreDesc;
	
	public long transactionVolume;
	public long transactionCount;
	public String transactionVolumeDesc;
	public double transactionAvgResponse;
	public double transactionResponseDelta;
	
	public long failureVolume;
	public long failureCount;
	
	public String failureCountDesc;
	public double failureRate;
	public String failureRateDelta;
	
	public long errorVolume;
	public int errorCount;
	
	public String errorCountDesc;

	
	public String reliabilityState;
	public String statusName;

	public String failureDesc;
	public String relabilityDesc;
	
	public String alertStatus;
	public String alertDesc;
	public String alertViewId;

	public int connectedClients;
	public String connectedClientsDesc;
	
	public String eventName;
	public String eventDesc;
	public String eventTypeDesc;
	public String eventType;
	public String eventApp;
	
	public String dashboardId;
	public String dashboardField;
	public String dashboardValue;
	
	public ReliabilityReportRow(Series<ReliabilityReportRow> series, int index) {
				
		this.service = series.getString(ReliabilityReportInput.SERVICE, index);
		this.serviceId = series.getString(ReliabilityReportInput.SERVICE_ID, index);

		this.key = series.getString(ReliabilityReportInput.KEY, index);
		this.name = series.getString(EventsInput.NAME, index);
		
		this.previousDepName = series.getString(ReliabilityReportInput.PREV_DEP_NAME, index);
		this.previousDepFrom = series.getString(ReliabilityReportInput.PREV_DEP_FROM, index);			
		this.previousDepState = series.getString(ReliabilityReportInput.PREV_DEP_STATE, index);
		
		this.newIssues = series.getString(ReliabilityReportInput.NEW_ISSUES, index);
		this.regressions = series.getString(ReliabilityReportInput.REGRESSIONS, index);
		this.slowdowns = series.getString(ReliabilityReportInput.SLOWDOWNS, index); 
		
		this.newIssuesDesc = series.getString(ReliabilityReportInput.NEW_ISSUES_DESC, index);
		this.regressionsDesc = series.getString(ReliabilityReportInput.REGRESSIONS_DESC, index);
		this.slowdownsDesc = series.getString(ReliabilityReportInput.SLOWDOWNS_DESC, index);
		
		this.score = series.getDouble(ReliabilityReportInput.SCORE, index); 
		this.scoreDesc = series.getString(ReliabilityReportInput.SCORE_DESC, index);
		
		this.transactionVolume = series.getLong(ReliabilityReportInput.TRANSACTION_VOLUME, index);
		this.transactionCount = series.getInt(ReliabilityReportInput.TRANSACTION_COUNT, index);
		this.transactionAvgResponse = series.getLong(ReliabilityReportInput.TRANSACTION_AVG_RESPONSE, index);
		this.transactionResponseDelta = series.getDouble(ReliabilityReportInput.TRANSACTION_RESPONSE_DELTA, index);
		
		this.failureVolume = series.getInt(ReliabilityReportInput.TRANSACTION_FAILURES, index);
		this.transactionVolumeDesc = series.getString(ReliabilityReportInput.TRANSACTION_VOLUME_DESC, index);
		this.failureCountDesc = series.getString(ReliabilityReportInput.TRANSACTION_FAIL_COUNT_DESC, index);
		this.failureCount = series.getLong(ReliabilityReportInput.TRANSACTION_FAILURES_COUNT, index);
		this.failureRate = series.getDouble(ReliabilityReportInput.TRANSACTION_FAIL_RATE, index);
		this.failureRateDelta = series.getString(ReliabilityReportInput.TRANSACTION_FAIL_RATE_DELTA, index);
		
		this.errorVolume = series.getLong(ReliabilityReportInput.ERROR_VOLUME, index);
		this.errorCountDesc = series.getString(ReliabilityReportInput.ERROR_COUNT_DESC, index);
		this.errorCount = series.getInt(ReliabilityReportInput.ERROR_COUNT, index);
		
		this.reliabilityState = series.getString(ReliabilityReportInput.RELIABILITY_STATE, index);
		this.failureDesc = series.getString(ReliabilityReportInput.TRANSACTION_FAIL_DESC, index);
		this.relabilityDesc = series.getString(ReliabilityReportInput.RELIABILITY_DESC, index);	
		this.statusName = series.getString(ReliabilityReportInput.STATUS_NAME, index);
		
		this.alertStatus = series.getString(ReliabilityReportInput.ALERT_STATUS, index);
		this.alertDesc = series.getString(ReliabilityReportInput.ALERT_DESC, index);
		this.alertViewId = series.getString(ReliabilityReportInput.ALERT_VIEW_ID, index);
		
		this.connectedClients = series.getInt(ReliabilityReportInput.CONNECTED_CLIENTS, index);
		this.connectedClientsDesc = series.getString(ReliabilityReportInput.CONNECTED_CLIENTS_DESC, index);
		
		this.eventName = series.getString(ReliabilityReportInput.EVENT_NAME, index);
		this.eventDesc = series.getString(ReliabilityReportInput.EVENT_DESC, index);
		this.eventTypeDesc = series.getString(ReliabilityReportInput.EVENT_TYPE_DESC, index);
		this.eventType = series.getString(ReliabilityReportInput.EVENT_TYPE, index);
		this.eventApp = series.getString(ReliabilityReportInput.EVENT_APP, index);
		
		this.dashboardId = series.getString(ReliabilityReportInput.DASHBOARD_ID, index);
		this.dashboardField = series.getString(ReliabilityReportInput.DASHBOARD_FIELD, index);
		this.dashboardValue = series.getString(ReliabilityReportInput.DASHBOARD_VALUE, index);		
	}
}
