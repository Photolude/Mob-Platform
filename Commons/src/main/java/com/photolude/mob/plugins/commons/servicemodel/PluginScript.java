package com.photolude.mob.plugins.commons.servicemodel;

public class PluginScript {
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
	
	@Override
	public boolean equals(Object otherObject)
	{
		boolean retval = false;
		
		if(otherObject != null && PluginScript.class.isAssignableFrom(otherObject.getClass()))
		{
			PluginScript other = (PluginScript)otherObject;
			
			retval = other.getType().equals(this.type) && other.getScript().equals(this.script);
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
		return this.getType() + "," + this.getScript();
	}
}
