package com.mob.www.platform.services;

public class AnonymousAccess implements IAnonymousAccess {
	
	private String defaultIdentity;
	@Override
	public String getDefaultIdentity(){ return this.defaultIdentity; }
	public AnonymousAccess setDefaultIdentity(String value)
	{
		this.defaultIdentity = value;
		return this;
	}
	
	private String[] availableIdentities;
	public String[] getAvailableIdentities(){ return this.availableIdentities; }
	public AnonymousAccess setAvailableIdentities(String[] value)
	{
		this.availableIdentities = value;
		return this;
	}
	
	private String[] userNames;
	public String[] getUserNames(){ return this.userNames; }
	public AnonymousAccess setUserNames(String[] value)
	{
		this.userNames = value;
		return this;
	}
	
	@Override
	public boolean isAnonymousUsername(String username) {
		if(this.userNames == null || username == null)
		{
			return false;
		}
		
		for(String name : this.userNames)
		{
			if(name != null && name.toLowerCase().equals(username.toLowerCase()))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean isAnonymousIdentity(String identity) {
		if(this.availableIdentities == null)
		{
			return false;
		}
		
		for(String id : this.availableIdentities)
		{
			if(id != null && id.equals(identity))
			{
				return true;
			}
		}
		
		return false;
	}
}
