package com.takipi.api.client.request.team;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.result.team.ServiceUsersResultMessage;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

import java.util.List;
import java.util.Map;

public class ChangeTeamMembersRolesRequest extends ServiceRequest implements ApiPostRequest<ServiceUsersResultMessage>
{
	private final List<TeamMember> teamMembers;
	
	protected ChangeTeamMembersRolesRequest(String serviceId, List<TeamMember> teamMembers) {
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
	public Class<ServiceUsersResultMessage> resultClass() {
		return ServiceUsersResultMessage.class;
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
		
		@Override
		protected void validate()
		{
			super.validate();
			
			if (this.members.size() == 0)
			{
				throw new IllegalArgumentException("Request is empty");
			}
			
			for (TeamMember teamMember : this.members)
			{
				if ((Strings.isNullOrEmpty(teamMember.email)) ||
					(Strings.isNullOrEmpty(teamMember.role)))
				{
					throw new IllegalArgumentException("User email or role cannot be empty");
				}
				
				if (!((teamMember.role.equalsIgnoreCase("Owner")) ||
					  (teamMember.role.equalsIgnoreCase("Admin")) ||
					  (teamMember.role.equalsIgnoreCase("Member"))))
				{
					throw new IllegalArgumentException("User role must be Owner/Admin/Member");
				}
			}
		}
		
		public ChangeTeamMembersRolesRequest build() {
			validate();
			
			return new ChangeTeamMembersRolesRequest(serviceId, this.members);
		}
	}
}
