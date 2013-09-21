package com.photolude.mob.commons.plugins.servicemodel;

public class WebPage {
	private String name;
	public String getName(){ return this.name; }
	public WebPage setName(String value)
	{
		this.name = value;
		return this;
	}
	
	private int id;
	public int getId(){ return this.id; }
	public WebPage setId(int value)
	{
		this.id = value;
		return this;
	}
}
