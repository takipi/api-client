package com.takipi.api.client.request.team;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.ServiceUsers;
import com.takipi.api.client.data.team.ServiceUsers.TeamMember;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;

import java.util.List;

public class ChangeTeamMembersRolesRequest extends ServiceRequest implements ApiPostRequest<EmptyResult>
{
	private final ServiceUsers serviceUsers;
	
	protected ChangeTeamMembersRolesRequest(String serviceId, ServiceUsers serviceUsers) {
		super(serviceId);
		
		this.serviceUsers = serviceUsers;
	}
	
	@Override
	public String postData() {
		return ((new Gson()).toJson(serviceUsers));
	}
	
	@Override
	public Class<EmptyResult> resultClass() {
		return EmptyResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/team/change-role";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private List<TeamMember> members = Lists.newArrayList();
		
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder addTeamMemberNewRole(String email, String role) {
			TeamMember teamMember = new TeamMember();
			teamMember.email = email;
			teamMember.role = role;
			
			this.members.add(teamMember);
			
			return this;
		}
		
		public ChangeTeamMembersRolesRequest build() {
			validate();
			
			ServiceUsers serviceUsers = new ServiceUsers();
			serviceUsers.team_members = this.members;
			
			return new ChangeTeamMembersRolesRequest(serviceId, serviceUsers);
		}
	}
}
