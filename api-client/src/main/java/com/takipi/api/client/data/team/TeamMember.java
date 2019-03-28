package com.takipi.api.client.data.team;

public class TeamMember {
	public String name;
	public String email;
	public String role;
	public String state;
	public String link;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(email);
		builder.append('(');
		builder.append(role);
		builder.append(')');

		return builder.toString();
	}
}
