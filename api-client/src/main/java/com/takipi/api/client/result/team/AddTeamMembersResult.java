package com.takipi.api.client.result.team;

import java.util.List;

import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.core.result.intf.ApiResult;

public class AddTeamMembersResult implements ApiResult {
	public List<TeamMember> new_members;
}
