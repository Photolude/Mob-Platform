package com.mob.commons.plugins.servicemodel;

public class MainMenuItem {
	private int id;
	public int getId(){return this.id; }
	public void setId(int value){ this.id = value; }
	
	private String displayName;
	public String getDisplayName(){ return this.displayName; }
	public void setDisplayName(String value){ this.displayName = value; }
	
	private String iconData;
	public String getIconData(){ return this.iconData; }
	public void setIconData(String value){ this.iconData = value; }
	
	private String target;
	public String getTarget(){ return this.target; }
	public void setTarget(String value){ this.target = value; }
	
	private int priority;
	public int getPriority(){ return this.priority; }
	public void setPriority(int value){ this.priority = value; }
}
