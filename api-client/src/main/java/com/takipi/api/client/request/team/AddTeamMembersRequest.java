package com.takipi.api.client.request.team;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.result.team.ServiceUsersResult;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddTeamMembersRequest extends ServiceRequest implements ApiPostRequest<ServiceUsersResult>
{
	private final List<TeamMember> teamMembers;
	
	protected AddTeamMembersRequest(String serviceId, List<TeamMember> teamMembers) {
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
	public Class<ServiceUsersResult> resultClass() {
		return ServiceUsersResult.class;
	}
	
	@Override
	public String urlPath() {
		return baseUrlPath() + "/team";
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder extends ServiceRequest.Builder {
		private Set<String> emailsToAdd;
		
		Builder() {
			emailsToAdd = Sets.newHashSet();
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
			
			if (this.emailsToAdd.isEmpty())
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
		
		public AddTeamMembersRequest build() {
			validate();
			
			List<TeamMember> teamMembers = Lists.newArrayList();
			
			for (String email: this.emailsToAdd)
			{
				TeamMember teamMember = new TeamMember();
				teamMember.email = email;
				
				teamMembers.add(teamMember);
			}
			
			return new AddTeamMembersRequest(serviceId, teamMembers);
		}
	}
}

