package com.takipi.api.client.data.label;

public class Label {
	public String name;
	public String type;
	public String color;

	@Override
	public String toString() {
		return (name != null ? name : super.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (!(obj instanceof Label))) {
			return false;
		}

		Label other = (Label) obj;

		return ((this.name != null) && (other.name != null) && (name.equals(other.name)));
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : super.hashCode());
	}
}
