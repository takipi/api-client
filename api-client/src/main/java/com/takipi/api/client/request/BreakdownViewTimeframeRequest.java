package com.takipi.api.client.request;

import com.takipi.api.client.request.event.BreakdownType;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

public abstract class BreakdownViewTimeframeRequest extends ViewTimeframeRequest
{
	private final boolean breakServers;
	private final boolean breakApps;
	private final boolean breakDeployments;
	
	public BreakdownViewTimeframeRequest(String serviceId, String viewId, String from, String to, boolean raw, Collection<String> servers,
			Collection<String> apps, Collection<String> deployments, boolean breakServers,
			boolean breakApps, boolean breakDeployments) {
		super(serviceId, viewId, from, to, raw, servers, apps, deployments);
		
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
	protected int fillParams(String[] params, int startIndex) throws UnsupportedEncodingException
	{
		int index = super.fillParams(params, startIndex);
		
		params[index++] = "breakServers=" + Boolean.toString(breakServers);
		params[index++] = "breakApps=" + Boolean.toString(breakApps);
		params[index++] = "breakDeployments=" + Boolean.toString(breakDeployments);
		
		return index;
	}
	
	public abstract static class Builder extends ViewTimeframeRequest.Builder {
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