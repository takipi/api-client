package com.takipi.api.client.request.functions;

import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiDeleteRequest;

public class DeleteFunctionRequest extends ServiceRequest implements ApiDeleteRequest<EmptyResult> {
	private final String libraryId;

	DeleteFunctionRequest(String serviceId, String libraryId) {
		super(serviceId);

		this.libraryId = libraryId;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/udfs/" + libraryId;
	}

	@Override
	public String postData() {
		return null;
	}

	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private String libraryId;

		Builder() {

		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder setLibraryId(String libraryId) {
			this.libraryId = libraryId;

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (!ValidationUtil.isLegalLibraryId(libraryId)) {
				throw new IllegalArgumentException("Illegal library id - " + libraryId);
			}
		}

		public DeleteFunctionRequest build() {
			validate();

			return new DeleteFunctionRequest(serviceId, libraryId);
		}
	}
}
