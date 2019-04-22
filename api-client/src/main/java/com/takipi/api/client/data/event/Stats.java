package com.takipi.api.client.data.event;

public class Stats extends BaseStats {
	public String machine_name;
	public String application_name;
	public String deployment_name;

	@Override
	public Stats clone() {
		Stats result = new Stats();

		result.hits = this.hits;
		result.invocations = this.invocations;

		result.machine_name = this.machine_name;
		result.application_name = this.application_name;
		result.deployment_name = this.deployment_name;

		return result;
	}
}
