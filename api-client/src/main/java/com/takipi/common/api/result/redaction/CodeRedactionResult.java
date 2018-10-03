package com.takipi.common.api.result.redaction;

import com.takipi.common.api.result.intf.ApiResult;

public class CodeRedactionResult implements ApiResult {
	public CodeRedactionElements include;
	public CodeRedactionElements exclude;
}
