package com.takipi.api.client.functions.output;

import com.takipi.api.client.functions.input.TransactionsListInput;

public class TransactionRow implements SeriesRow {
	
	public static class Reader implements SeriesReader<TransactionRow> {

		@Override
		public TransactionRow read(Series<TransactionRow> series, int index) {
			return new TransactionRow(series, index);
		}
		
		@Override
		public Class<? extends SeriesRow> rowType() {
			return TransactionRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return null;
		}
	}
	
	
	/**
	 * Field values documented in matching TransactionsListInput constants
	 */
	
	public String link;
	public String transaction;	
	
	public long invocations;
	public double avg_response;
	public String time_consumed;
	
	public int slow_state;	
	public double error_rate;
	
	public long errors;
	public String error_rate_state;
	public String error_rate_delta_state;	
	public String error_rate_delta_desc;
	public String error_description;
	public String delta_description;
	
	public double baseline_avg;
	public String baseline_calls;
	public String active_calls;	
	
	public TransactionRow(Series<TransactionRow> series, int index) {
				
		this.link = series.getString(TransactionsListInput.LINK, index);
		this.transaction = series.getString(TransactionsListInput.TRANSACTION, index);
		
		this.invocations = series.getLong(TransactionsListInput.TOTAL, index);
		this.avg_response = series.getDouble(TransactionsListInput.AVG_RESPONSE, index);
		this.time_consumed = series.getString(TransactionsListInput.TIME_COMSUMED, index);
		this.slow_state = series.getInt(TransactionsListInput.SLOW_STATE, index);

		this.error_rate = series.getDouble(TransactionsListInput.ERROR_RATE, index);
		this.errors = series.getLong(TransactionsListInput.ERRORS, index);
		this.error_rate_state = series.getString(TransactionsListInput.ERROR_RATE_DELTA, index);

		this.error_rate_delta_state = series.getString(TransactionsListInput.ERROR_RATE_DELTA_STATE, index);
		this.error_rate_delta_desc = series.getString(TransactionsListInput.ERROR_RATE_DELTA_DESC, index);
		this.error_description = series.getString(TransactionsListInput.ERRORS_DESC, index);
		
		this.delta_description = series.getString(TransactionsListInput.DELTA_DESC, index);
		this.baseline_avg = series.getDouble(TransactionsListInput.BASELINE_AVG, index);
		
		this.baseline_calls =  series.getString(TransactionsListInput.BASELINE_CALLS, index);
		this.active_calls = series.getString(TransactionsListInput.ACTIVE_CALLS, index);
	}
}
