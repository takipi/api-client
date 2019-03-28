package com.takipi.api.client.request.team;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.team.ChangeTeamMembersResult;
import com.takipi.api.core.request.intf.ApiDeleteRequest;
import com.takipi.common.util.JsonUtil;

public class RemoveTeamMembersRequest extends ServiceRequest implements ApiDeleteRequest<ChangeTeamMembersResult> {
	private final Collection<String> usersToRemove;

	RemoveTeamMembersRequest(String serviceId, Collection<String> usersToRemove) {
		super(serviceId);

		this.usersToRemove = usersToRemove;
	}

	@Override
	public String postData() {
		List<TeamMember> teamMembers = Lists.newArrayList();

		for (String userToRemove : usersToRemove) {
			TeamMember teamMember = new TeamMember();
			teamMember.email = userToRemove;

			teamMembers.add(teamMember);
		}

		Map<String, String> map = ImmutableMap.of("team_members", (new Gson()).toJson(teamMembers));

		return JsonUtil.createSimpleJson(map, false);
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/team";
	}

	@Override
	public Class<ChangeTeamMembersResult> resultClass() {
		return ChangeTeamMembersResult.class;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private Set<String> usersToRemove;

		Builder() {
			this.usersToRemove = Sets.newHashSet();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder addTeamMemberToRemove(String email) {
			if (Strings.isNullOrEmpty(email)) {
				throw new IllegalArgumentException("User email cannot be empty");
			}

			this.usersToRemove.add(email);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (this.usersToRemove.isEmpty()) {
				throw new IllegalArgumentException("Request is empty");
			}
		}

		public RemoveTeamMembersRequest build() {
			validate();

			return new RemoveTeamMembersRequest(serviceId, usersToRemove);
		}
	}
}
