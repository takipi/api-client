package com.takipi.api.client.result.process;

import java.util.List;

import com.takipi.api.client.data.process.Jvm;
import com.takipi.api.core.result.intf.ApiResult;

public class JvmsResult implements ApiResult {
	public List<Jvm> clients;
}
