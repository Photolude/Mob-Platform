package com.mob.commons.plugins.servicemodel;

public class PluginScript {
	private int id;
	public int getId(){ return this.id; }
	public void setId(int value){ this.id = value; }
	
	private String name;
	public String getName(){ return this.name; }
	public void setName(String value){ this.name = value; }
	
	private String script;
	public String getScript(){ return this.script; }
	public void setScript(String value){ this.script = value; }
	
	private String type;
	public String getType(){ return this.type; }
	public void setType(String value){ this.type = value; }
	
	private String page;
	public String getPage(){ return this.page; }
	public void setPage(String value){ this.page = value; }
	
	public PluginScript()
	{
	}
	
	public PluginScript(int id, String name, String script, String type, String page)
	{
		this.id = id;
		this.name = name;
		this.script = script;
		this.type = type;
		this.page = page;
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
