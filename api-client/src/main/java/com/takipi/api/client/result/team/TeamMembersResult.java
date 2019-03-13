package com.takipi.api.client.result.team;

import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.core.result.intf.ApiResult;

import java.util.List;

public class TeamMembersResult implements ApiResult
{
	public List<TeamMember> team_members;
}
