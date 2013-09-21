package com.photolude.mob.user.dal;

public interface ILogonAccessLayer {
	Long attemptLogOn(String username, String password);
}
