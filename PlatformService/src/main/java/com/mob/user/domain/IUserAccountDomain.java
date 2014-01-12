package com.mob.user.domain;

public interface IUserAccountDomain {
	String logon(String username, String password);
	String logonViaGoogle(String token);
	
	void logoff(String temporaryId);
	boolean isLoggedIn(String temporaryId);
	Long getStaticIdFromToken(String temporaryId);
	Long getStaticIdFromEmail(String email, String source);
}
