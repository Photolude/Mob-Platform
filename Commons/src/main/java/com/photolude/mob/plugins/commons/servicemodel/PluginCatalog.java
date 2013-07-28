package com.photolude.mob.plugins.commons.servicemodel;

import java.util.Arrays;

public class PluginCatalog {
	private PluginDefinition[] plugins;
	public PluginDefinition[] getPlugins(){ return this.plugins; }
	public PluginCatalog setPlugins(PluginDefinition[] value)
	{
		this.plugins = value;
		return this;
	}
	
	private PluginDefinition[] userPlugins;
	public PluginDefinition[] getUserPlugins(){ return this.userPlugins; }
	public PluginCatalog setUserPlugins(PluginDefinition[] value)
	{
		this.userPlugins = value;
		return this;
	}
	
	@Override
	public boolean equals(Object otherObject)
	{
		boolean retval = false;
		
		if(otherObject != null && otherObject instanceof PluginCatalog)
		{
			PluginCatalog other = (PluginCatalog)otherObject;
			
			retval = Arrays.equals(this.plugins, other.plugins) && Arrays.equals(this.userPlugins, other.userPlugins);
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
		if(this.userPlugins != null)
		{
			for(PluginDefinition plugin : this.userPlugins)
			{
				retval.append(plugin.toString());
			}
		}
		retval.append("}");
		
		return retval.toString();
	}
}
