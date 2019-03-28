package com.takipi.api.client.request.team;

import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.team.AddTeamMembersResult;
import com.takipi.api.client.util.validation.ValidationUtil.UserRole;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.JsonUtil;

public class AddTeamMembersRequest extends ServiceRequest implements ApiPostRequest<AddTeamMembersResult> {
	private final Map<String, UserRole> newUsers;

	AddTeamMembersRequest(String serviceId, Map<String, UserRole> newUsers) {
		super(serviceId);

		this.newUsers = newUsers;
	}

	@Override
	public String postData() {
		List<TeamMember> teamMembers = Lists.newArrayList();

		for (Map.Entry<String, UserRole> entry : newUsers.entrySet()) {
			TeamMember teamMember = new TeamMember();
			teamMember.email = entry.getKey();
			teamMember.role = entry.getValue().name();

			teamMembers.add(teamMember);
		}

		Map<String, String> map = ImmutableMap.of("team_members", (new Gson()).toJson(teamMembers));

		return JsonUtil.createSimpleJson(map, false);
	}

	@Override
	public Class<AddTeamMembersResult> resultClass() {
		return AddTeamMembersResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/team";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private Map<String, UserRole> newUsers;

		Builder() {
			newUsers = Maps.newHashMap();
		}

		@Override
		public Builder setServiceId(String serviceId) {
			super.setServiceId(serviceId);

			return this;
		}

		public Builder addTeamMember(String email) {
			return addTeamMember(email, UserRole.member);
		}

		public Builder addTeamMember(String email, UserRole role) {
			if (Strings.isNullOrEmpty(email)) {
				throw new IllegalArgumentException("User email cannot be empty");
			}

			if ((role != UserRole.member) && (role != UserRole.viewer)) {
				throw new IllegalArgumentException("Illegal user role " + role);
			}

			this.newUsers.put(email, role);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (this.newUsers.isEmpty()) {
				throw new IllegalArgumentException("Request is empty");
			}
		}

		public AddTeamMembersRequest build() {
			validate();

			return new AddTeamMembersRequest(serviceId, newUsers);
		}
	}
}
