package com.mob.www.platform.model;

public class ExternalPostRequest {
	private String data;
	public String getData(){ return this.data; }
	public ExternalPostRequest setData(String value)
	{
		this.data = value;
		return this;
	}
	
	private String request;
	public String getRequest(){ return this.request; }
	public ExternalPostRequest setRequest(String value)
	{
		this.request = value;
		return this;
	}
	
	private String requestDataType;
	public String getRequestDataType(){ return this.requestDataType; }
	public ExternalPostRequest setRequestDataType(String value)
	{
		this.requestDataType = value;
		return this;
	}
}
