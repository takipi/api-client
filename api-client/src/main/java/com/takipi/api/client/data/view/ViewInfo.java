package com.takipi.api.client.data.view;

public class ViewInfo {
	public String id;
	public String name;
	public String description;
	public boolean shared;
	public boolean immutable;
	public ViewFilters filters;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (name != null) {
			sb.append(name);
		}

		if (id != null) {
			sb.append("(");
			sb.append(id);
			sb.append(")");
		}

		if (sb.length() != 0) {
			return sb.toString();
		}

		return super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (!(obj instanceof ViewInfo))) {
			return false;
		}

		ViewInfo other = (ViewInfo) obj;

		return ((this.id != null) && (other.id != null) && (id.equals(other.id)));
	}

	@Override
	public int hashCode() {
		return (id != null ? id.hashCode() : super.hashCode());
	}
}
