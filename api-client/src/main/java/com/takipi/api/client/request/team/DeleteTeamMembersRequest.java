package com.takipi.api.client.request.team;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.result.team.ServiceUsersResultMessage;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiDeleteRequest;
import com.takipi.common.util.JsonUtil;

import java.util.List;
import java.util.Map;

public class DeleteTeamMembersRequest extends ServiceRequest implements ApiDeleteRequest<ServiceUsersResultMessage>
{
	private final List<TeamMember> teamMembers;
	
	DeleteTeamMembersRequest(String serviceId, List<TeamMember> teamMembers) {
		super(serviceId);
		
		this.teamMembers = teamMembers;
	}
	
	@Override
	public String postData() {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(1);
		
		map.put("team_members", (new Gson()).toJson(teamMembers));
		
		return JsonUtil.createSimpleJson(map, false);
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/team";
	}
	
	@Override
	public Class<ServiceUsersResultMessage> resultClass() {
		return ServiceUsersResultMessage.class;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private List<String> usersToRemove = Lists.newArrayList();
		
		Builder() {
		
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder addTeamMemberToDelete(String email) {
			this.usersToRemove.add(email);
			
			return this;
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			
			if (this.usersToRemove.isEmpty())
			{
				throw new IllegalArgumentException("Request is empty");
			}
			
			for (String email : usersToRemove)
			{
				if (Strings.isNullOrEmpty(email))
				{
					throw new IllegalArgumentException("User email cannot be empty");
				}
			}
		}
		
		public DeleteTeamMembersRequest build() {
			validate();
			
			List<TeamMember> teamMembers = Lists.newArrayList();
			
			for (String email: this.usersToRemove)
			{
				TeamMember teamMember = new TeamMember();
				teamMember.email = email;
				
				teamMembers.add(teamMember);
			}
			
			return new DeleteTeamMembersRequest(serviceId, teamMembers);
		}
	}
}

