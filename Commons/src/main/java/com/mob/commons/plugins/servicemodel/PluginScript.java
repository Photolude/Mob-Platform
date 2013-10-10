package com.mob.commons.plugins.servicemodel;

public class PluginScript {
	private int id;
	public int getId(){ return this.id; }
	public PluginScript setId(int value)
	{
		this.id = value;
		return this;
	}
	
	private String name;
	public String getName(){ return this.name; }
	public PluginScript setName(String value)
	{
		this.name = value;
		return this;
	}
	
	private String script;
	public String getScript(){ return this.script; }
	public PluginScript setScript(String value)
	{
		this.script = value;
		return this;
	}
	
	private String type;
	public String getType(){ return this.type; }
	public PluginScript setType(String value)
	{
		this.type = value;
		return this;
	}
	
	private String page;
	public String getPage(){ return this.page; }
	public PluginScript setPage(String value)
	{
		this.page = value;
		return this;
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		boolean retval = false;
		
		if(otherObject != null && PluginScript.class.isAssignableFrom(otherObject.getClass()))
		{
			PluginScript other = (PluginScript)otherObject;
			
			retval = other.getType().equals(this.type) && other.getScript().equals(this.script) 
					&& ((other.getName() == null && this.name == null) || (other.getName() != null && other.getName().equals(this.name)));
		}
		
		return retval;
	}
	
	@Override
	public int hashCode()
	{
		return (this.toString()).hashCode();
	}
	
	public String toString()
	{
		return this.getName() + "," + this.getType() + "," + this.getScript();
	}
}
