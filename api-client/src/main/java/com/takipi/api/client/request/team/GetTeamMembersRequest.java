package com.takipi.api.client.request.team;

import com.takipi.api.client.result.team.TeamMembersResult;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiGetRequest;

public class GetTeamMembersRequest extends ServiceRequest implements ApiGetRequest<TeamMembersResult>
{
	protected GetTeamMembersRequest (String serviceId) {
		super(serviceId);
	}
	
	@Override
	public Class<TeamMembersResult> resultClass() {
			return TeamMembersResult.class;
		}
	
	@Override
	public String urlPath() {
			return baseUrlPath() + "/team";
			}
	
	public static Builder newBuilder() {
			return new Builder();
			}
	
	public static class Builder extends ServiceRequest.Builder {
		Builder() {
		
		}
		
		@Override
		public GetTeamMembersRequest.Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public GetTeamMembersRequest build() {
			validate();
			
			return new GetTeamMembersRequest(serviceId);
		}
	}
}
