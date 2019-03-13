package com.takipi.api.client.result.team;

import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.core.result.intf.ApiResult;

import java.util.List;

public class NewTeamMembersResult implements ApiResult
{
	public List<TeamMember> new_members;
}
