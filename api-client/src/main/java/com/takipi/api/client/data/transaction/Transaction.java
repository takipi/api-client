package com.takipi.api.client.data.transaction;

public class Transaction {
	public String name;
	public String class_name;
	public String method_name;
	public String method_desc;

	public Stats stats;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if ((obj == null) || (!(obj instanceof Transaction))) {
			return false;
		}

		Transaction other = (Transaction) obj;

		return ((this.name != null) && (other.name != null) && (name.equals(other.name)));
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : super.hashCode());
	}
}
