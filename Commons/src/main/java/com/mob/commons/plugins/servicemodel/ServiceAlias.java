package com.mob.commons.plugins.servicemodel;

public class ServiceAlias {
	private String name;
	public String getName(){ return this.name; }
	public ServiceAlias setName(String value)
	{
		this.name = value;
		return this;
	}
	
	private String endpoint;
	public String getEndpoint(){ return this.endpoint; }
	public ServiceAlias setEndpoint(String value)
	{
		this.endpoint = value;
		return this;
	}
}
