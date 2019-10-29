package com.takipi.integrations.functions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Function {
	
	public enum FunctionType {
		Annotation,
		Variable,
		Graph,
		Table,
		SingleStat
	}
	
	String name();  
	FunctionType type();  
	String description();
	String image();  
	String example();  
	boolean isInternal();  
}
