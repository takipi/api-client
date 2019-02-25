package com.takipi.api.client.data.advanced;

import com.takipi.api.core.result.intf.ApiResult;

public class AdvancedSettings implements ApiResult {
	public String allowed_ips;
	public boolean show_rethrows;
	public boolean show_log_links;
	public boolean clear_env_filters;
}
