package com.mob.www.platform.services;

public class ServiceCallParts {
	private String alias;
	public String getAlias(){ return this.alias; }
	public ServiceCallParts setAlias(String value)
	{
		this.alias = value;
		return this;
	}
	
	private String pathParams;
	public String getPathParams(){ return this.pathParams; }
	public ServiceCallParts setPathParams(String value)
	{
		this.pathParams = value;
		return this;
	}
	
	public ServiceCallParts(String serviceCall)
	{
		if(serviceCall == null)
		{
			return;
		}
		
		this.alias = serviceCall;
		
		int firstSlash = serviceCall.indexOf('/');
		if(firstSlash >= 0 && serviceCall.indexOf('/', firstSlash + 1) > 0)
		{
			int secondSlash = serviceCall.indexOf('/', firstSlash + 1);
			alias = serviceCall.substring(0, secondSlash);
			this.pathParams = serviceCall.substring(secondSlash, serviceCall.length());
		}
	}
}
