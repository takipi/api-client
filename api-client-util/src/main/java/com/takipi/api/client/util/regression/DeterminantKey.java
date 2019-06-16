package com.takipi.api.client.util.regression;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

public class DeterminantKey implements Comparable<DeterminantKey> {

	public static final DeterminantKey Empty = DeterminantKey.create(null, null, null);

	public final String determinantKey;

	private DeterminantKey(String determinantKey) {
		this.determinantKey = determinantKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DeterminantKey)) {
			return false;
		}

		DeterminantKey other = (DeterminantKey) obj;

		return determinantKey.equals(other.determinantKey);
	}

	@Override
	public int hashCode() {
		return determinantKey.hashCode();
	}

	@Override
	public int compareTo(DeterminantKey other) {
		return determinantKey.compareTo(other.determinantKey);
	}

	public static DeterminantKey create(String machineName, String agentName, String deploymentName) {
		List<String> determinantValues = new ArrayList<String>();

		if (!Strings.isNullOrEmpty(machineName)) {
			determinantValues.add(machineName);
		}

		if (!Strings.isNullOrEmpty(agentName)) {
			determinantValues.add(agentName);
		}

		if (!Strings.isNullOrEmpty(deploymentName)) {
			determinantValues.add(deploymentName);
		}

		String determinantKey = String.join("_", determinantValues);

		if (Strings.isNullOrEmpty(determinantKey)) {
			determinantKey = "ALL";
		}

		return new DeterminantKey(determinantKey);
	}
}
