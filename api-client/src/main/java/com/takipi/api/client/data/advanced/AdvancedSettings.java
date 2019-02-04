package com.takipi.api.client.data.advanced;

import com.takipi.api.core.result.intf.ApiResult;

public class AdvancedSettings implements ApiResult
{
	String allowed_ips;
	Boolean show_rethrows;
	Boolean show_log_links;
	Boolean clear_determinant_filters;
	
	public AdvancedSettings(String allowed_ips, Boolean show_rethrows, Boolean show_log_links, Boolean clear_determinant_filters)
	{
		this.allowed_ips = allowed_ips;
		this.show_rethrows = show_rethrows;
		this.show_log_links = show_log_links;
		this.clear_determinant_filters = clear_determinant_filters;
	}
}
