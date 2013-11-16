package com.mob.plugin.dal.model;

public class GetPageScriptsRequest {
	public long staticUserId;
	public String page;
	
	public GetPageScriptsRequest(long staticUserId, String page)
	{
		this.staticUserId = staticUserId;
		this.page = page;
	}
}
