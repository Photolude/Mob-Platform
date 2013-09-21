package com.photolude.mob.commons.servicemodel.logon;

public class LogonRequest {
	private String userName;
	public String getUserName(){ return this.userName; }
	public LogonRequest setUserName(String value)
	{
		this.userName = value;
		return this;
	}
	
	private String password;
	public String getPassword(){ return this.password; }
	public LogonRequest setPassword(String value)
	{
		this.password = value;
		return this;
	}
}
