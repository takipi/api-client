package com.takipi.api.client.request.team;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.result.team.TeamMembersResultMessage;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.util.validation.ValidationUtil;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

import java.util.List;
import java.util.Map;

public class ChangeTeamMembersRolesRequest extends ServiceRequest implements ApiPostRequest<TeamMembersResultMessage>
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
	public Class<TeamMembersResultMessage> resultClass() {
		return TeamMembersResultMessage.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/team/change-role";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private Map<String, String> membersRoles;
		
		Builder() {
			this.membersRoles = Maps.newHashMap();
		}
		
		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);
			
			return this;
		}
		
		public Builder addTeamMemberNewRole(String email, String role) {
			this.membersRoles.put(email, role);
			
			return this;
		}
		
		@Override
		protected void validate()
		{
			super.validate();
			
			if (this.membersRoles.isEmpty())
			{
				throw new IllegalArgumentException("Request is empty");
			}
			
			for (String email : this.membersRoles.keySet())
			{
				if (Strings.isNullOrEmpty(email))
				{
					throw new IllegalArgumentException("User email must not be empty");
				}
			}
			
			for (String memberRole : this.membersRoles.values())
			{
				if (!ValidationUtil.isLegalUserRole(memberRole))
				{
					throw new IllegalArgumentException("User role must be Owner/Admin/Member");
				}
			}
		}
		
		public ChangeTeamMembersRolesRequest build() {
			validate();
			
			List<TeamMember> teamMembers = Lists.newArrayList();
			
			for (String email : membersRoles.keySet())
			{
				TeamMember teamMember = new TeamMember();
				teamMember.email = email;
				teamMember.role = membersRoles.get(email);
				
				teamMembers.add(teamMember);
			}
			
			return new ChangeTeamMembersRolesRequest(serviceId, teamMembers);
		}
	}
}
