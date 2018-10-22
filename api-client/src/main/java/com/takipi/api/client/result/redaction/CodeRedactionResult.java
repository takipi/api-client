package com.takipi.api.client.result.redaction;

import com.takipi.api.core.result.intf.ApiResult;

public class CodeRedactionResult implements ApiResult {
	public CodeRedactionElements include;
	public CodeRedactionElements exclude;
}
