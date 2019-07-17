package com.takipi.api.client.request;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

import com.takipi.api.client.request.event.BreakdownType;

public abstract class BreakdownEventsTimeframeRequest extends EventsTimeframeRequest {
	public final boolean breakServers;
	public final boolean breakApps;
	public final boolean breakDeployments;

	public BreakdownEventsTimeframeRequest(String serviceId, Collection<String> eventIds, String from, String to,
			Collection<String> servers, Collection<String> apps, Collection<String> deployments, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, eventIds, from, to, servers, apps, deployments);

		this.breakServers = breakServers;
		this.breakApps = breakApps;
		this.breakDeployments = breakDeployments;
	}

	@Override
	protected int paramsCount() {
		// Three slots for breakdown.
		//
		return super.paramsCount() + 3;
	}

	@Override
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException {
		int index = super.fillParams(params, startIndex);

		params[index++] = "breakServers=" + Boolean.toString(breakServers);
		params[index++] = "breakApps=" + Boolean.toString(breakApps);
		params[index++] = "breakDeployments=" + Boolean.toString(breakDeployments);

		return index;
	}

	public abstract static class Builder extends EventsTimeframeRequest.Builder {
		protected boolean breakServers;
		protected boolean breakApps;
		protected boolean breakDeployments;

		public Builder setBreakServers(boolean breakServers) {
			this.breakServers = breakServers;

			return this;
		}

		public Builder setBreakApps(boolean breakApps) {
			this.breakApps = breakApps;

			return this;
		}

		public Builder setBreakDeployments(boolean breakDeployments) {
			this.breakDeployments = breakDeployments;

			return this;
		}

		public Builder setBreakFilters(Set<BreakdownType> breakdownTypes) {
			if (breakdownTypes != null) {
				if (breakdownTypes.contains(BreakdownType.App)) {
					this.setBreakApps(true);
				}

				if (breakdownTypes.contains(BreakdownType.Deployment)) {
					this.setBreakDeployments(true);
				}

				if (breakdownTypes.contains(BreakdownType.Server)) {
					this.setBreakServers(true);
				}
			}

			return this;
		}
	}
}
