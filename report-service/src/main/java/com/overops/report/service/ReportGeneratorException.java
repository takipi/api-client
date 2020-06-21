package com.overops.report.service;

/**
 * Generic exception to throw for this integration project
 */
public class ReportGeneratorException extends RuntimeException {

	private static final long serialVersionUID = 1371433862659956717L;
	
	public ReportGeneratorException(String message) {
        super(message);
    }

    public ReportGeneratorException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
