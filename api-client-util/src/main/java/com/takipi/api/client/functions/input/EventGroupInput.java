package com.takipi.api.client.functions.input;

import java.util.Arrays;
import java.util.List;

public class EventGroupInput extends EventsInput{
	
	public static enum GroupType {
		CodeLocation,
		Applications,
		Deployment,
		ServerGroup,
		None
	}
	
	public String groupType;
	
	public GroupType getGroupType() {
		
		if (groupType == null) {
			return GroupType.CodeLocation;
		}
	
		GroupType result = GroupType.valueOf(groupType.replace(" ", ""));
		
		return result;
	}
	
	public static final String DEPLOYMENT = "Deployment";
	public static final String APPLICATION = "Application";
	public static final String SERVER_GROUP = "Server Group";
	public static final String EVENTS = "Events";
	public static final String EVENT_LOCATIONS = "Event Locations";
	public static final String NEW_EVENTS = "New Events";
	public static final String FIRST_EVENT = "First Event";
	public static final String LAST_EVENT = "Last Event";
	public static final String DEPLOYMENTS = "Deployments";
	public static final String APPLICATIONS = "Applications";
	
	public static final List<String> APP_FIELDS = Arrays.asList(
			new String[] { 	
				APPLICATION, EVENTS, EVENT_LOCATIONS, 
				DEPLOYMENTS 
			});

	public static final List<String> DEP_FIELDS = Arrays.asList(
			new String[] { 	
				DEPLOYMENT, EVENTS, EVENT_LOCATIONS, NEW_EVENTS,
				FIRST_EVENT, LAST_EVENT, APPLICATIONS 
			});
	
	public static final List<String> SERVER_FIELDS = Arrays.asList(
			new String[] { 	
				SERVER_GROUP, EVENTS, EVENT_LOCATIONS, 
				APPLICATIONS 
			});
}
