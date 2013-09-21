package com.photolude.mob.commons.service.clients;

public interface IUserServiceClient {
	String logon(String email, String password);
	void logout(String userToken);
	Long getStaticIdFromToken(String token);
}
