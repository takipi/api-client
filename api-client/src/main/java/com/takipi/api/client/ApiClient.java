package com.takipi.api.client;

import com.takipi.api.client.observe.Observer;
import com.takipi.api.core.request.intf.ApiDeleteRequest;
import com.takipi.api.core.request.intf.ApiGetRequest;
import com.takipi.api.core.request.intf.ApiPostBytesRequest;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.api.core.request.intf.ApiPutRequest;
import com.takipi.api.core.result.intf.ApiResult;
import com.takipi.api.core.url.UrlClient.Response;

public interface ApiClient {
	public int getApiVersion();

	public String getHostname();

	public <T extends ApiResult> Response<T> get(ApiGetRequest<T> request);

	public <T extends ApiResult> Response<T> put(ApiPutRequest<T> request);

	public <T extends ApiResult> Response<T> post(ApiPostRequest<T> request);

	public <T extends ApiResult> Response<T> post(ApiPostBytesRequest<T> request);

	public <T extends ApiResult> Response<T> delete(ApiDeleteRequest<T> request);

	public void addObserver(Observer observer);
}
