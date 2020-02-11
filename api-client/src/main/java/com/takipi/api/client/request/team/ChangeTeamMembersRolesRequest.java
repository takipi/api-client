package com.takipi.api.client.request.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.takipi.api.client.data.team.TeamMember;
import com.takipi.api.client.request.ServiceRequest;
import com.takipi.api.client.result.team.ChangeTeamMembersResult;
import com.takipi.api.client.util.validation.ValidationUtil.UserRole;
import com.takipi.api.core.request.intf.ApiPostRequest;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.JsonUtil;
import com.takipi.common.util.StringUtil;

public class ChangeTeamMembersRolesRequest extends ServiceRequest implements ApiPostRequest<ChangeTeamMembersResult> {
	private final Map<String, UserRole> membersRoles;

	ChangeTeamMembersRolesRequest(String serviceId, Map<String, UserRole> membersRoles) {
		super(serviceId);

		this.membersRoles = membersRoles;
	}

	@Override
	public String postData() {
		List<TeamMember> teamMembers = new ArrayList<>();

		for (Map.Entry<String, UserRole> entry : membersRoles.entrySet()) {
			TeamMember teamMember = new TeamMember();
			teamMember.email = entry.getKey();
			teamMember.role = entry.getValue().name();

			teamMembers.add(teamMember);
		}

		Map<String, String> map = CollectionUtil.mapOf("team_members", (new Gson()).toJson(teamMembers));

		return JsonUtil.createSimpleJson(map, false);
	}

	@Override
	public Class<ChangeTeamMembersResult> resultClass() {
		return ChangeTeamMembersResult.class;
	}

	@Override
	public String urlPath() {
		return baseUrlPath() + "/team/change-role";
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends ServiceRequest.Builder {
		private Map<String, UserRole> membersRoles;

		Builder() {
			this.membersRoles = new HashMap<>();
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
			if (StringUtil.isNullOrEmpty(email)) {
				throw new IllegalArgumentException("User email cannot be empty");
			}

			if (role == null) {
				throw new IllegalArgumentException("New role cannot be empty");
			}

			this.membersRoles.put(email, role);

			return this;
		}

		@Override
		protected void validate() {
			super.validate();

			if (this.membersRoles.isEmpty()) {
				throw new IllegalArgumentException("Request is empty");
			}
		}

		public ChangeTeamMembersRolesRequest build() {
			validate();

			return new ChangeTeamMembersRolesRequest(serviceId, membersRoles);
		}
	}
}
