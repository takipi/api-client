package com.takipi.api.client.result.label;

import java.util.List;

import com.takipi.api.client.data.label.Label;
import com.takipi.api.core.result.intf.ApiResult;

public class LabelsResult implements ApiResult {
	public List<Label> labels;
}
