package com.photolude.mob.plugins.commons.servicemodel;

public class MainMenuItem {
	private String displayName;
	public String getDisplayName(){ return this.displayName; }
	public MainMenuItem setDisplayName(String value)
	{
		this.displayName = value;
		return this;
	}
	
	private String iconData;
	public String getIconData(){ return this.iconData; }
	public MainMenuItem setIconData(String value)
	{
		this.iconData = value;
		return this;
	}
	
	private String target;
	public String getTarget(){ return this.target; }
	public MainMenuItem setTarget(String value)
	{
		this.target = value;
		return this;
	}
	
	
}
