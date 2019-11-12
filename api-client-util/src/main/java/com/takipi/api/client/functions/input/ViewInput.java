package com.takipi.api.client.functions.input;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Objects;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

/**
 * The base input for all functions that operate on events nested within a target view,
 * whose entry points match a list of selected entry points  within a selected time range.
 *
 */
public abstract class ViewInput extends EnvironmentsFilterInput {
	
	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="All Events",
			description = "The name of the view to query for events. For example: \"All Events\".")
	public String view;

	@Param(type=ParamType.String, advanced=false, literals={}, defaultValue="",
			description = "A comma delimited list of entry points in either simple class name or simple class name + method format.\n" + 
				"For example: \"myServlet,\"myOtherServlet.doGet\" will only choose events whose entry point\n" + 
				"is \"myServlet\" (regardless of a method name) or whose entry point class is \"myOtherServlet\" and\n" + 
				"method name is \"doGet\".")
	public String transactions;
	
	@Param(type=ParamType.String, advanced=true, literals={}, defaultValue="",
			description = "A time filter denoting the time range in which this query operates. The format os the time filter\n" + 
					"should match the Grafana time range format: http://docs.grafana.org/reference/timerange/")
	public String timeFilter;
	
	@Param(type=ParamType.Boolean, advanced=true, literals={}, defaultValue="false",
			description = "A value indicating whether this timeFilter is passed from within a template variable")
	public boolean varTimeFilter;

	/**
	 * A series type name for an empty results set
	 */
	public static final String NO_DATA_SERIES = "no_data_series";
	
	/**
	 * A field name for the start of the active window for the report row
	 */
	public static final String FROM = "from";
	
	/**
	 * A field name for the end of the active window for the report row
	 */
	public static final String TO = "to";
	
	/**
	 *A field name for the Time range (e.g. 12h, 7d) for the report row
	 */
	public static final String TIME_RANGE = "timeRange";
	
	public boolean hasTransactions() {
		return hasFilter(transactions);
	}
	
	public Collection<String> getTransactions(String serviceId) {

		if (!hasTransactions()) {
			return Collections.emptyList();
		}

		Collection<String> result = getServiceFilters(transactions, serviceId, true);
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!super.equals(obj)) {
			return false;
		}
		
		if (!(obj instanceof ViewInput)) {
			return false;
		}
		
		ViewInput other = (ViewInput)obj;
		
		return Objects.equal(view, other.view) 
				&& Objects.equal(transactions, other.transactions)
				&& Objects.equal(timeFilter, other.timeFilter);
	}
	
	@Override
	public int hashCode() {
		
		if (view != null) {
			return super.hashCode() ^ view.hashCode();
		}
		
		return super.hashCode();
	}
}

