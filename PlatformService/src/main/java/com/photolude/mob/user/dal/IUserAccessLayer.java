package com.photolude.mob.user.dal;

import java.util.Date;

public interface IUserAccessLayer {
	boolean setTemporaryUserId(Long staticUserId, String temporaryId, Date expiration);
	void removeTemporaryUserId(String temporaryId);
	UserLogonData getLogonData(String temporaryId);
	Long getUserIdFromToken(String temporaryId);
	Long getUserIdFromEmail(String email);
}
