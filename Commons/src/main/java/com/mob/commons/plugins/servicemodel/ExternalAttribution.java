package com.mob.commons.plugins.servicemodel;

public class ExternalAttribution {
	private String link;
	public String getLink(){return this.link; }
	public ExternalAttribution setLink(String value)
	{
		this.link = value;
		return this;
	}
	
	private String reason;
	public String getReason(){ return this.reason; }
	public ExternalAttribution setReason(String value)
	{
		this.reason = value;
		return this;
	}
}
