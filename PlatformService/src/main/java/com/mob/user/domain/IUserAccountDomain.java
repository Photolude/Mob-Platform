package com.mob.user.domain;

public interface IUserAccountDomain {
	String logon(String username, String password);
	String logonViaSource(String token, String source);
	
	void logoff(String temporaryId);
	boolean isLoggedIn(String temporaryId);
	Long getStaticIdFromToken(String temporaryId);
	Long getStaticIdFromEmail(String email, String source);
}
