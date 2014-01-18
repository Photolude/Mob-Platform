package com.mob.www.platform.model;

public class GooglePlusLogin {
	private String token;
	public String getToken(){ return this.token; }
	public GooglePlusLogin setToken(String value)
	{
		this.token = value;
		return this;
	}
}
