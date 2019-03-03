package com.takipi.api.client.observe;

public interface Observer {

	public enum Operation {
		GET, DELETE, POST, PUT
	}

	public void observe(Operation operation, String url, String request, String response, int responseCode, long time);
}
