package com.photolude.mob.www.platform.model;

public class LogonRequest {
	private String email;
	public String getEmail(){ return this.email; }
	public LogonRequest setEmail(String value)
	{
		this.email = value;
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
