package com.photolude.mob.user.domain;

public interface IUserAccountDomain {
	String logon(String username, String password);
	void logoff(String temporaryId);
	boolean isLoggedIn(String temporaryId);
	Long getStaticIdFromToken(String temporaryId);
	Long getStaticIdFromEmail(String email);
}
