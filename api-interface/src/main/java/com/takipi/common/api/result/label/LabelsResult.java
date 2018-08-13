package com.takipi.common.api.result.label;

import java.util.List;

import com.takipi.common.api.result.intf.ApiResult;

public class LabelsResult implements ApiResult {
	public List<Label> labels;

	public static class Label {
		public String name;
		public String type;
		public String color;
	}
}
