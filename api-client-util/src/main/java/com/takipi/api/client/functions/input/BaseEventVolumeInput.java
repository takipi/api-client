package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

/**
 * The base input for all function operating on event volume data that requires
 * the specification of a graph point resolution from the Overops REST API.
 *
 */
public abstract class BaseEventVolumeInput extends EventFilterInput {
	
	public static final String CRITICAL_EXCEPTIONS = "Critical Exceptions";
	public static final String TRANSACTION_FAILURES = "Transaction Failures";

	/**
	 * These values must match literals of VolumeType for annotations
	 */
	public static final String VOLUME_TYPE_HITS = "hits";
	public static final String VOLUME_TYPE_INVOCATIONS = "invocations";
	public static final String VOLUME_TYPE_ALL = "all";
	
	/**
	 * These values must match literals of PerformanceState for annotations
	 */
	public static final String CRITICAL = "Critical";
	public static final String SLOWING = "Slowing";
	public static final String OK = "OK";

	public static final List<String> TRANSACTION_STATES = Arrays.asList(new String[] {
		OK, SLOWING, CRITICAL	
		});
	
	public static final int OK_ORDINAL = TRANSACTION_STATES.indexOf(OK);
	public static final int SLOWING_ORDINAL = TRANSACTION_STATES.indexOf(SLOWING);
	public static final int CRITICAL_ORDINAL = TRANSACTION_STATES.indexOf(CRITICAL);	
}
