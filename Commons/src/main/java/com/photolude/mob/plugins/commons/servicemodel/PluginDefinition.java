package com.photolude.mob.plugins.commons.servicemodel;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PluginDefinition {
	private int id;
	public int getId(){ return this.id; }
	public PluginDefinition setId(int value)
	{
		this.id = value;
		return this;
	}
	
	private String name;
	public String getName() { return this.name; }
	public PluginDefinition setName(String value) 
	{ 
		this.name = value;
		return this;
	}
	
	private String company;
	public String getCompany() { return this.company; }
	public PluginDefinition setCompany(String value)
	{
		this.company = value;
		return this;
	}
	
	private String version;
	public String getVersion() { return this.version; }
	public PluginDefinition setVersion(String value)
	{
		this.version = value;
		return this;
	}
	
	private String role;
	public String getRole(){return this.role; }
	public PluginDefinition setRole(String value)
	{
		this.role = value;
		return this;
	}
	
	private String icon;
	public String getIcon(){ return this.icon; }
	public PluginDefinition setIcon(String value)
	{
		this.icon = value;
		return this;
	}
	
	private String rawExternalResources;
	public String getRawExternalResources(){ return this.rawExternalResources; }
	public PluginDefinition setRawExternalResources(String value)
	{
		this.rawExternalResources = value;
		return this;
	}
	
	public String[] getExternalResources()
	{
		if(this.rawExternalResources != null && this.rawExternalResources.length() > 0)
		{
			return this.rawExternalResources.split(";");
		}
		
		return new String[0];
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		boolean retval = false;
		
		if(otherObject != null && PluginDefinition.class.isAssignableFrom(otherObject.getClass()))
		{
			PluginDefinition other = (PluginDefinition)otherObject;
			
			retval = this.getId() == other.getId() &&
					this.getName().equals(other.getName()) &&
					this.getCompany().equals(other.getCompany()) &&
					this.getVersion().equals(other.getVersion());
		}
		
		return retval;
	}
	
	@Override
	public int hashCode()
	{
		return (this.getId() + "," + this.getName() + "," + this.getCompany() + "," + this.getVersion()).hashCode();
	}
	
	@Override
	public String toString()
	{
		return "Name:" + this.name + ", company:" + this.company + ", version:" + this.version;
	}
}
