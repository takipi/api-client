package com.takipi.api.client.functions.output;

import com.takipi.api.client.functions.input.RegressionsInput;

public class RegressionRow extends BaseEventRow implements SeriesRow {

	public static class Reader implements SeriesReader<RegressionRow> {

		@Override
		public RegressionRow read(Series<RegressionRow> series, int index) {
			return new RegressionRow(series, index);
		}
		
		@Override
		public Class<? extends SeriesRow> rowType() {
			return RegressionRow.class;
		}

		@Override
		public Class<? extends SeriesHeader> headerType() {
			return null;
		}	
	}

	/**
	 * Field values documented in matching RegressionsInput constants
	 */
	
	public String regression_type;
	public double reg_delta;
	public String regression;
	public int severity;
	public String reg_desc;
	
	public RegressionRow(Series<RegressionRow> series, int index) {
		
		super(series, index);
			
		this.regression_type = series.getString(RegressionsInput.REGRESSION_TYPE, index);
		this.reg_delta = series.getDouble(RegressionsInput.REG_DELTA, index);
		this.regression = series.getString(RegressionsInput.REGRESSION, index);
		this.severity = series.getInt(RegressionsInput.SEVERITY, index);
		this.reg_desc = series.getString(RegressionsInput.REG_DESC, index);		
	}
}
