package com.takipi.api.client;

import com.takipi.api.client.observe.Observer;
import com.takipi.common.util.Pair;

import java.util.Collection;
import java.util.Map;

public class UnsafeRemoteApiClient extends RemoteApiClient
{
	UnsafeRemoteApiClient(String hostname, Pair<String, String> auth, int apiVersion, int connectTimeout, int readTimeout,
			LogLevel defaultLogLevel, Map<Integer, LogLevel> responseLogLevels, Collection<Observer> observers)
	{
		super(hostname, auth, apiVersion, connectTimeout, readTimeout, defaultLogLevel, responseLogLevels, observers);
	}
	
	public static Builder newBuilder()
	{
		return new Builder();
	}
	
	public static class Builder extends RemoteApiClient.Builder
	{
		@Override
		protected Pair<String, String> getAuth()
		{
			return null;
		}
	}
}
