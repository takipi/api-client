package com.takipi.api.client.data.team;

import com.takipi.api.core.result.intf.ApiResult;

import java.util.List;

public class ServiceUsers implements ApiResult
{
	public List<TeamMember> team_members;
	
	public static class TeamMember
	{
		public String name;
		public String email;
		public String role;
		public String state;
		public String link;
	}
}
