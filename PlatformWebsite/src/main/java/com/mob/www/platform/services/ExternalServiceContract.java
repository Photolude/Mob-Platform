package com.mob.www.platform.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.commons.plugins.servicemodel.ServiceAlias;

public class ExternalServiceContract implements IServiceContracts {
	private static final Logger logger = Logger.getLogger(ExternalServiceContract.class);
	
	public void setExternalServices(PluginPage page, ServiceCallContext context)
	{
		Map<String,String> externalServices = new HashMap<String,String>();
		
		for(PluginDefinition plugin : page.getPlugins())
		{
			if(plugin.getServiceAliases() != null)
			{
				for(ServiceAlias service : plugin.getServiceAliases())
				{
					String key = plugin.getRole() + "/" + service.getName();
					if(!externalServices.containsKey(key))
					{
						externalServices.put(key, service.getEndpoint());
					}
				}
			}
		}
		
		context.setServiceLookup(externalServices);
	}
	
	public boolean isCallAllowed(String serviceCall, ServiceCallContext context)
	{
		if(serviceCall == null || context == null)
		{
			return false;
		}
		
		String alias = serviceCall;
		int firstSlash = serviceCall.indexOf('/');
		if(firstSlash >= 0 && serviceCall.indexOf('/', firstSlash + 1) > 0)
		{
			int secondSlash = serviceCall.indexOf('/', firstSlash + 1);
			alias = serviceCall.substring(0, secondSlash);
		}
		
		Map<String,String> externalServices = context.getServiceLookup();
		if(externalServices != null)
		{
			return externalServices.containsKey(alias);
		}
		
		return false;
	}
	
	public String getEndpointFromAlias(String serviceCall, ServiceCallContext context)
	{
		if(serviceCall == null || context == null)
		{
			return null;
		}
		
		Map<String,String> externalServices = context.getServiceLookup();
		ServiceCallParts parts = new ServiceCallParts(serviceCall);
		
		if(externalServices == null || !externalServices.containsKey(parts.getAlias()))
		{
			logger.warn("Either the system is misconfigured, or a service call was attempted which is not available. " + serviceCall);
			return null;
		}
		
		return externalServices.get(parts.getAlias());
	}
}
