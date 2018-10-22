package com.takipi.api.client.result.redaction;

import java.util.List;

import com.takipi.api.core.result.intf.ApiResult;

public class CodeRedactionElements implements ApiResult {
	public List<String> classes;
	public List<String> packages;
}
