package com.takipi.api.client.request.team;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.ServiceUsers;
import com.takipi.api.client.data.team.ServiceUsers.TeamMember;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.EmptyResult;
import com.takipi.api.core.request.intf.ApiPostRequest;

import java.util.List;

public class UpdateTeamMembersRequest extends ServiceRequest implements ApiPostRequest<EmptyResult>
{
	private final ServiceUsers serviceUsers;
	
	protected UpdateTeamMembersRequest(String serviceId, ServiceUsers serviceUsers) {
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
		return baseUrlPath() + "/team";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private List<String> emailsToAdd = Lists.newArrayList();
		
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder addTeamMember(String email) {
			this.emailsToAdd.add(email);
			
			return this;
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			
			if (this.emailsToAdd.size() == 0)
			{
				throw new IllegalArgumentException("Request is empty");
			}
			
			for (String email : emailsToAdd)
			{
				if (Strings.isNullOrEmpty(email))
				{
					throw new IllegalArgumentException("User email cannot be empty");
				}
			}
		}
		
		public UpdateTeamMembersRequest build() {
			validate();
			
			ServiceUsers serviceUsers = new ServiceUsers();
			serviceUsers.team_members = Lists.newArrayList();
			
			for (String email: this.emailsToAdd)
			{
				TeamMember teamMember = new TeamMember();
				teamMember.email = email;
				
				serviceUsers.team_members.add(teamMember);
			}
			
			return new UpdateTeamMembersRequest(serviceId, serviceUsers);
		}
	}
}

