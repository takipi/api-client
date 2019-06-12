package com.takipi.api.client.util.regression;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class DeterminantKey implements Comparable<DeterminantKey> {
	
	public static final DeterminantKey Empty = DeterminantKey.create(null, null, null);
	
	public final String determinantKey;
	
	private DeterminantKey(String determinantKey) {
		this.determinantKey = determinantKey;
	}
	
	public static DeterminantKey create(String machineName, String agentName, String deploymentName) {
		List<String> determinantValues = new ArrayList<String>();
		
		if (machineName != null) {
			determinantValues.add(machineName);
		}
		
		if (agentName != null) {
			determinantValues.add(agentName);
		}
		
		if (deploymentName != null) {
			determinantValues.add(deploymentName);
		}
		
		String determinantKey = String.join("_", determinantValues);
		
		if (Strings.isNullOrEmpty(determinantKey)) {
			determinantKey = "ALL";
		}
		
		return new DeterminantKey(determinantKey);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof  DeterminantKey)) {
			return false;
		}
		
		return ((DeterminantKey) (obj)).determinantKey.equals(determinantKey);
	}
	
	@Override
	public int hashCode() {
		return determinantKey.hashCode();
	}
	
	@Override
	public int compareTo(DeterminantKey other) {
		return determinantKey.compareTo(other.determinantKey);
	}
}
