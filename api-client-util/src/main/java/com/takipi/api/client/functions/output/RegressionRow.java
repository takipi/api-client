package com.takipi.api.client.functions.output;

import com.takipi.api.client.functions.input.RegressionsInput;

public class RegressionRow extends EventRow {

	public static class Reader extends BaseSeriesReader {

		@Override
		public SeriesRow read(Series series, int index) {
			return new RegressionRow(series, index);
		}
		
		@Override
		public Class<? extends SeriesRow> rowType() {
			return RegressionRow.class;
		}	
	}

	public String regression_type;
	public double reg_delta;
	public String regression;
	public int severity;
	public String reg_desc;
	
	public RegressionRow(Series series, int index) {
		
		super(series, index);
			
		this.regression_type = series.getString(RegressionsInput.REGRESSION_TYPE, index);
		this.reg_delta = series.getDouble(RegressionsInput.REG_DELTA, index);
		this.regression = series.getString(RegressionsInput.REGRESSION, index);
		this.severity = series.getInt(RegressionsInput.SEVERITY, index);
		this.reg_desc = series.getString(RegressionsInput.REG_DESC, index);		
	}
}
