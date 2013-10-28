package com.mob.www.platform.services;

import com.mob.commons.plugins.servicemodel.PluginPage;

public interface IServiceContracts {
	void setExternalServices(PluginPage page, ServiceCallContext context);
	boolean isCallAllowed(String serviceCall, ServiceCallContext context);
	
	String getEndpointFromAlias(String serviceCall, ServiceCallContext context);
}
