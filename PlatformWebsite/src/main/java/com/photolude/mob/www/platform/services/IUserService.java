package com.photolude.mob.www.platform.services;

public interface IUserService {
	String logon(String email, String password);
	void logout(String userToken);
}
