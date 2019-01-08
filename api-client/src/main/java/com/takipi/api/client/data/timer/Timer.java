package com.takipi.api.client.data.timer;

import java.util.List;

import com.takipi.api.core.result.intf.ApiResult;

public class Timer implements ApiResult {
	public String id;
	public String class_name;
	public String method_name;
	public long threshold;
	public List<String> servers;
	public List<String> applications;
	public List<String> deployments;
	boolean is_enabled;
}
