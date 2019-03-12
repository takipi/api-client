package com.takipi.api.client.data.team;

import com.takipi.api.core.result.intf.ApiResult;

import java.util.List;

public class ServiceUsersResponse implements ApiResult
{
	public List<ServiceUsers.TeamMember> new_members;
}
