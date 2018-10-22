package com.takipi.api.client.result.functions;

import java.util.List;

import com.takipi.api.client.data.functions.UserLibrary;
import com.takipi.api.core.result.intf.ApiResult;

public class FunctionsResult implements ApiResult {
	public List<UserLibrary> libraries;
}
