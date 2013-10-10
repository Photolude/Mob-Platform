package com.mob.commons.plugins.servicemodel;

public class PluginDataCall {
	private int id;
	public int getId(){ return this.id; }
	public PluginDataCall setId(int value)
	{
		this.id = value;
		return this;
	}
	
	private String method;
	public String getMethod(){ return this.method; }
	public PluginDataCall setMethod(String value)
	{
		this.method = value;
		return this;
	}
	
	private String uri;
	public String getUri(){ return this.uri; }
	public PluginDataCall setUri(String value)
	{
		this.uri = value;
		return this;
	}
	
	private String pageVariable;
	public String getPageVariable(){ return this.pageVariable; }
	public PluginDataCall setPageVariable(String value)
	{
		this.pageVariable = value;
		return this;
	}
	
	private String content;
	public String getContent(){ return this.content; }
	public PluginDataCall setContent(String value)
	{
		this.content = value;
		return this;
	}
	
	private String contentType;
	public String getContentType(){ return this.contentType; }
	public PluginDataCall setContentType(String value)
	{
		this.contentType = value;
		return this;
	}
}
