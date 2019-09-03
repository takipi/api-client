package com.takipi.api.client.functions.input;

/**
 * The base input for all function operating on event volume data that requires
 * the specification of a graph point resolution from the Overops REST API.
 *
 */
public abstract class BaseEventVolumeInput extends EventFilterInput {
	
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

	

}
