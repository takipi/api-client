package com.takipi.common.api.request;

public abstract class GlobalSettingsRequest extends BaseRequest {
	protected GlobalSettingsRequest() {

	}

	protected String baseUrlPath() {
		return "/global-settings";
	}

	public static class Builder {
		protected Builder() {

		}

		protected void validate() {

		}
	}
}
