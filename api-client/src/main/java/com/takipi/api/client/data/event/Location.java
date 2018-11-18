package com.takipi.api.client.data.event;

import com.google.common.base.Objects;

public class Location {
	public String prettified_name;
	public String class_name;
	public String method_name;
	public String method_desc;
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Location)) {
			return false;
		}
		
		Location other = (Location)obj;
		
		if (!Objects.equal(class_name, other.class_name)) {
			return false;
		}
		
		if (!Objects.equal(method_name, other.method_name)) {
			return false;
		}
		
		if (!Objects.equal(method_desc, other.method_desc)) {
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
		
		return result.toString().hashCode();	
	}
	
	@Override
	public String toString() {
		return class_name + "." + method_name;
	}
}
