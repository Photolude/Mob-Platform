package com.mob.commons.plugins.servicemodel;

import java.util.Arrays;

public class PluginPage {
	private PluginDefinition[] plugins;
	public PluginDefinition[] getPlugins(){ return this.plugins; }
	public PluginPage setPlugins(PluginDefinition[] value)
	{
		this.plugins = value;
		return this;
	}
	
	private PluginScript[] scripts;
	public PluginScript[] getScripts(){ return this.scripts; }
	public PluginPage setScripts(PluginScript[] value)
	{
		this.scripts = value;
		return this;
	}
	
	private PluginDataCall[] dataCalls;
	public PluginDataCall[] getDataCalls(){ return this.dataCalls; }
	public PluginPage setDataCalls(PluginDataCall[] value)
	{
		this.dataCalls = value;
		return this;
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		boolean retval = false;
		
		if(otherObject != null && PluginPage.class.isAssignableFrom(otherObject.getClass()))
		{
			PluginPage other = (PluginPage)otherObject;
			
			retval = Arrays.equals(this.plugins, other.plugins) && Arrays.equals(this.scripts, other.scripts);
		}
		
		return retval;
	}
	
	@Override
	public int hashCode()
	{
		return (this.toString()).hashCode();
	}
	
	@Override
	public String toString()
	{
		StringBuilder retval = new StringBuilder();
		
		retval.append("{");
		if(this.plugins != null)
		{
			for(PluginDefinition plugin : this.plugins)
			{
				retval.append(plugin.toString());
			}
		}
		retval.append("},{");
		if(this.scripts != null)
		{
			for(PluginScript script : this.scripts)
			{
				retval.append(script.toString());
			}
		}
		retval.append("}");
		
		return retval.toString();
	}
}
