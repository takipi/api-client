package com.takipi.common.api.result.redaction;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class CodeRedactionElements implements ApiResult {
	public List<String> classes;
	public List<String> packages;
}
