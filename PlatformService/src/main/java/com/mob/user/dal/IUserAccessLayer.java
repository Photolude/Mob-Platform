package com.mob.user.dal;

import java.util.Date;

public interface IUserAccessLayer {
	boolean setTemporaryUserId(Long staticUserId, String temporaryId, Date expiration, String sourceData);
	void removeTemporaryUserId(String temporaryId);
	UserLogonData getLogonData(String temporaryId);
	Long getUserIdFromToken(String temporaryId);
	Long getUserIdFromEmail(String email, String source);
	boolean addUser(String email, String userSource, String temporaryId, Date expiration, String sourceData);
}
