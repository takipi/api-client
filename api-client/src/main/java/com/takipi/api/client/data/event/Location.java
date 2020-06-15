package com.takipi.api.client.data.event;

import com.takipi.common.util.ObjectUtil;

public class Location {
	public String prettified_name;
	public String class_name;
	public String method_name;
	public String method_desc;
	public String code_last_modified;
	public int original_line_number = -1;
	public int method_position = -1;
	public boolean in_filter;
	public String source_file_path;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location)) {
			return false;
		}

		Location other = (Location) obj;

		if (!ObjectUtil.equal(class_name, other.class_name)) {
			return false;
		}

		if (!ObjectUtil.equal(method_name, other.method_name)) {
			return false;
		}

		if (!ObjectUtil.equal(method_desc, other.method_desc)) {
			return false;
		}

		if (method_position != other.method_position) {
			return false;
		}

		if (in_filter != other.in_filter) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		StringBuilder result = new StringBuilder();

		result.append(class_name);
		result.append(method_name);
		result.append(method_desc);
		result.append(method_position);

		return result.toString().hashCode();
	}

	@Override
	public String toString() {
		return class_name + "." + method_name;
	}
}
