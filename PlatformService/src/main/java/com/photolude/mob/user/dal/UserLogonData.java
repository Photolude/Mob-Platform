package com.photolude.mob.user.dal;

import java.util.Date;

public class UserLogonData {
	private long staticId;
	public long getStaticId(){ return this.staticId; }
	public UserLogonData setStaticId(long value)
	{
		this.staticId = value;
		return this;
	}
	
	private Date expiration;
	public Date getExpiration(){ return this.expiration; }
	public UserLogonData setExpiration(Date value)
	{
		this.expiration = value;
		return this;
	}
}
