package com.takipi.common.api.result.label;

import java.util.List;

import com.takipi.common.api.data.label.Label;
import com.takipi.common.api.result.intf.ApiResult;

public class LabelsResult implements ApiResult {
	public List<Label> labels;
}
