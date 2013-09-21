package com.photolude.mob.commons.plugins.servicemodel;

public class MainMenuItem {
	private int id;
	public int getId(){return this.id; }
	public MainMenuItem setId(int value)
	{
		this.id = value;
		return this;
	}
	
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
	
	private int priority;
	public int getPriority(){ return this.priority; }
	public MainMenuItem setPriority(int value)
	{
		this.priority = value;
		return this;
	}
}
