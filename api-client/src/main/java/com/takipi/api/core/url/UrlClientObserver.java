package com.takipi.api.core.url;

public interface UrlClientObserver {
	public enum Operation {
		GET, DELETE, POST, PUT
	}
	
	public void observe(Operation operation, String url, String response, long time);
}
