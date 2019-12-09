package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.common.util.ArrayUtil;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Param.ParamType;

@Function(name="events", type=FunctionType.Table,
description = "This function populates a table containing information about a list of events.", 
	example="events({\"fields\":\"link,type,entry_point,introduced_by,message,\n" + 
			" error_location,stats.hits,rate,first_seen\",\"view\":\"$view\",\n" + 
			" \"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\",\n" + 
			" \"applications\":\"$applications\",\"servers\":\"$servers\",\"deployments\":\"$deployments\",\n" + 
			" \"volumeType\":\"all\",\"maxColumnLength\":80, \"types\":\"$type\",\n" + 
			" \"pointsWanted\":\"$pointsWanted\",\"transactions\":\"$transactions\", \n" + 
			" \"searchText\":\"$search\"})\n", 
	image="https://drive.google.com/file/d/12CVPNc-FDBhWOpofo5Ofatjo-zhHo8qj/view?usp=sharing", isInternal=false)
public class EventsInput extends BaseEventVolumeInput {
	
	@Param(type=ParamType.String, advanced=false, literals={},
			description = "A comma delimited list of attributes defined in: https://doc.overops.com/reference#get_services-env-id-events-event-id " + 
				 "that can be used to define the columns within the result table. " + 
				 "Additional available fields described below." +
				 ID + ":id OverOps assigned ID for this unique event \n" +
				 SUMMARY + ": short text summary of the event \n" +
				 TYPE + ": the type of event: Uncaught Exception, Swallowed Exception, Logged Error,.. \n" +
				 NAME + ": The name of exception in case of exception (i.e. NullPointerException) otherwise the type\n" +
				 MESSAGE + ": The event's message if available\n" +
				 FIRST_SEEN + ": timestamp of when this unique even was first seen in this env\n" +
				 ERROR_LOCATION + ": the code location within app code (e.g. acme.foo()) from which this even originiates \n" +
				 ENTRY_POINT_NAME + ": the first frame within the event's call stack which falls within app code (e.g. myServlet.goGet()). \n" +
				 ERROR_ORIGIN + ": the frame in which this event took place, even if originating from within 3rd party of framecode code \n" +
				 INTRODUCED_BY + ": the deployment name in which this event was first seen \n" +
				 LABELS + ": a comma delimited array of labels applied to this event \n" +
				 SIMILAR_EVENT_UDS + ": a list of OverOps assigned Ids to events originating from the same code location but may have diff code entry points \n" +
				 IS_RETHROW + ": is this an exception that is a rethrow of another exception within the code \n" +
				 JIRA_ISSUE_URL + ": the URL of an issue assigned to this event \n" +
				 HITS + ": the number of times this event has happened \n" +
				 INVOCATIONS + ": the number of times the code location method has been invoked \n" +
				 ENV_ID + ": the event's env ID (e.g S12345)\n" +
				 TYPE_MESSAGE + ": a field containing a combination of the event type and message\n" +
				 JIRA_STATE + ": returns 1 if the event has a ticket assigned\n" +
				 RATE + ": the event rate hits / invocations\n" +
				 RATE_DESC + ": a text description of the event rate\n" +
				 DESCRIPTION + ": a text description of the error location, containing optional tier information\n" +
				 RATE_DELTA + ": a value showing the change between the event rate in the selected timeframe and the baseline\n" +
				 LAST_SEEN + ": a value showing  when this event was first observed\n" +
				 RATE_DELTA_DESC + ": a value describing the change between the event rate in the selected timeframe and the baseline\n" +
				 RANK + ": the order of this event within the returned list, based on its severity compared to other events within the return list\n" +
				 ENTRY_POINT_NAME + ": the simple name of the event entry point class name\n",	
			defaultValue = "")
	public String fields;
	
	public List<String> getFields() {
		
		if ((fields == null) || (fields.isEmpty())) {
			return DEFAULT_FIELDS;
		}
		
		String[] array = ArrayUtil.safeSplitArray(fields, ARRAY_SEPERATOR, true);
		return Arrays.asList(array);
	}
	
	public static final String EVENTS_SERIES = "events_series";
	
	public static final String JIRA_LABEL = "JIRA";

	public static final String ENV_ID = "env_id";
	public static final String ID = "id";
	public static final String SUMMARY = "summary";
	public static final String NAME = "name";
	public static final String MESSAGE = "message";
	public static final String TYPE = "type";
	public static final String FIRST_SEEN = "first_seen";
	public static final String ERROR_LOCATION = "error_location";
	public static final String ERROR_ORIGIN = "error_origin";
	public static final String INTRODUCED_BY = "introduced_by";
	public static final String HITS = "stats.hits";
	public static final String INVOCATIONS = "stats.invocations";
	public static final String LABELS = "labels";
	public static final String SIMILAR_EVENT_UDS = "similar_event_ids";
	public static final String IS_RETHROW = "is_rethrow";
	public static final String LINK = "link";
	public static final String TYPE_MESSAGE = "typeMessage";
	public static final String JIRA_STATE = "jira_state";	
	public static final String JIRA_ISSUE_URL = "jira_issue_url";
	public static final String STACK_FRAMES = "stack_frames";	
	public static final String RATE = "rate";	
	public static final String RATE_DESC = "rate_desc";
	public static final String DESCRIPTION = "description";	
	public static final String RATE_DELTA = "rate_delta";
	public static final String LAST_SEEN = "last_seen";
	public static final String RATE_DELTA_DESC = "rate_delta_desc";
	public static final String RANK = "rank";
	public static final String ENTRY_POINT_NAME = "entry_point_name";
	
	public static final List<String> DEFAULT_FIELDS = Arrays.asList(new String[] {
			ENV_ID, ID, SUMMARY, NAME, MESSAGE, TYPE, FIRST_SEEN, ERROR_LOCATION, ERROR_ORIGIN,
			INTRODUCED_BY, HITS, INVOCATIONS, LABELS, SIMILAR_EVENT_UDS,
			IS_RETHROW, LINK, TYPE_MESSAGE, JIRA_STATE, JIRA_ISSUE_URL, STACK_FRAMES,  RATE, RATE_DESC,
			DESCRIPTION, RATE_DELTA, LAST_SEEN, RATE_DELTA_DESC, RANK,
			ENTRY_POINT_NAME
	});
	
	@Param(type=ParamType.Enum, advanced=false, literals={VOLUME_TYPE_HITS, VOLUME_TYPE_INVOCATIONS, VOLUME_TYPE_ALL}, defaultValue=VOLUME_TYPE_ALL,
		description = " An optional value to control the volume data retrieved for objects in this query. If \"hits\" is specified,\n" + 
			"\"N/A\" will be returned for all values of the \"rate\" field (if selected).")
	public VolumeType volumeType;
	
	@Param(type=ParamType.Number, advanced=false, literals={}, defaultValue="0",
		description = "An optional value controlling the max string length of the message and typeMessage columns.") 
	public int maxColumnLength;
	
	@Param(type=ParamType.Number, advanced=false, literals={}, defaultValue="0",
		description = "An optional value controlling the max string length of the transaction and location columns.") 
	public int maxClassLength;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={}, defaultValue="false",
		description = "Controls whether events with the same code location but different entry points are grouped together. True to skip grouping") 
	public boolean skipGrouping;
	
	@Param(type=ParamType.Boolean, advanced=false, literals={}, defaultValue="false",
		description = "Controls whether events should break their stats by app .true to skip breakdown") 
	public boolean appBreakdown;
	
	@Param(type=ParamType.Number, advanced=false, literals={}, defaultValue="0",
			description = "An optional limit on the number of rows returned. Zero to ignore") 
	public int maxRows;
	
	public static final String Grid = "Grid";
	public static final String SingleStat = "SingleStat";

	@Param(type=ParamType.Enum, advanced=false, literals={Grid, SingleStat}, defaultValue="all",
			description = " The type of output returned by this function:\n" + 
			Grid + ": Output data in grid format\n" +
			SingleStat + ": Output a single stat denoting the number of events (rows) returned by this function")
	public String outputMode;
	
	public String getOutputMode() {
		
		if ((outputMode == null) || (outputMode.isEmpty())) {
			return Grid;
		}
		
		return outputMode;
	}
}
