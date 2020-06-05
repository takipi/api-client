package com.overops.report.service;

/**
 * Mark Unstable
 * <p>
 *     Enum used to control Job Flow
 * </p>
 */
public enum MarkUnstable
{
	/**
	 * Pause and wait for report; will fail CI job if Quality Gates fail or if there was an exception
	 */
	TRUE,
	
	/**
	 * Pause and wait for report; will always pass even if Quality Gates fail
	 */
	FALSE,
	
	/**
	 * No Pause; job will always pass because the report has not been generated yet
	 */
	LINK
}
