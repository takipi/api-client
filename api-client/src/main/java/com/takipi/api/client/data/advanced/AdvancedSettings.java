package com.takipi.api.client.data.advanced;

import com.takipi.api.core.result.intf.ApiResult;

public class AdvancedSettings implements ApiResult
{
	
	public AdvancedSettings(String allowed_ips, Boolean show_rethrows, Boolean show_log_links, Boolean clear_determinant_filters)
	{
		this.allowed_ips = allowed_ips;
		this.show_rethrows = show_rethrows;
		this.show_log_links = show_log_links;
		this.clear_determinant_filters = clear_determinant_filters;
	}
	public String allowed_ips;
	public boolean show_rethrows;
	public boolean show_log_links;
	public boolean clear_env_filters;
}
