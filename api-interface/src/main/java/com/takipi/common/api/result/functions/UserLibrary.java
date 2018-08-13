package com.takipi.common.api.result.functions;

import java.util.List;

public class UserLibrary {
	public String id;
	public String version;
	public String name;
	public String scope;
	public String key;
	public List<UserFunction> functions;

	public static class UserFunction {
		public String libraryId;
		public String functionId;
		public String type;
		public String functionName;
		public String description;
		public String paramType;
		public String defaultParams;
		public String classFile;
	}
}
