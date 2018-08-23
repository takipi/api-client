package com.takipi.common.api.data.view;

import java.util.List;

import com.takipi.common.api.data.view.filter.FirstSeenFilter;

public class ViewFilters {
	public FirstSeenFilter first_seen;
	public List<String> labels;
	public List<String> event_type;
	public List<String> event_name;
	public List<String> event_location;
	public List<String> event_package;
	public List<String> entry_point;
	public List<String> servers;
	public List<String> apps;
	public List<String> deployments;
	public List<String> introduced_by;
	public String search;
}
