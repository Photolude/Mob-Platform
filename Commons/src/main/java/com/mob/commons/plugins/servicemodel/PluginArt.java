package com.mob.commons.plugins.servicemodel;

public class PluginArt {
	private int id;
	public int getId(){ return this.id; }
	public PluginArt setId(int value)
	{
		this.id = value;
		return this;
	}
	
	private String contentType;
	public String getContentType(){ return this.contentType; }
	public PluginArt setContentType(String value)
	{
		this.contentType = value;
		return this;
	}
	
	private String data;
	public String getData(){ return this.data; }
	public PluginArt setData(String value)
	{
		this.data = value;
		return this;
	}
}
