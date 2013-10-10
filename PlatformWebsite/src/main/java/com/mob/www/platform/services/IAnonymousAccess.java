package com.mob.www.platform.services;

public interface IAnonymousAccess {
	boolean isAnonymousUsername(String username);
	boolean isAnonymousIdentity(String identity);
	
	String getDefaultIdentity();
}
