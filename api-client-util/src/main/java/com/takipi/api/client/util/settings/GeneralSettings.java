package com.takipi.api.client.util.settings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Strings;

public class GeneralSettings {

	/**
	 * Control whether events in table functions should group events from the same
	 * event location but different entry points into one row.
	 */
	public boolean group_by_entryPoint;

	/**
	 * The default list of event types (comma delimited) to be used when performing
	 * volume functions.
	 */
	public String event_types;
	
	/**
	 * A list of event types that each if found within the context of an entry point
	 * (transaction) call will mark that transaction as failed.Note that more than
	 * one of these events can take place within the context of a single transaction
	 * call (i.e. more than one log error can take place within the execution of a
	 * single entry point call)
	 */
	public String transaction_failures;

	public Collection<String> getDefaultTypes() {
		if (Strings.isNullOrEmpty(event_types)) {
			return Collections.emptyList();
		}

		String[] types = event_types.split(ServiceSettingsData.ARRAY_SEPERATOR);
		return Arrays.asList(types);
	}
}
