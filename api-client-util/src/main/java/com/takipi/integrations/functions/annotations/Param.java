package com.takipi.integrations.functions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
		
	public enum ParamType {
		Number,
		String,
		Boolean,
		Enum,
		Object
	}
	
	ParamType type();
	String[] literals();
	String description();
	String defaultValue();
	boolean advanced();
}
