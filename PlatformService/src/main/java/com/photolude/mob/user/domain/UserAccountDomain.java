package com.photolude.mob.user.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.photolude.mob.user.dal.ILogonAccessLayer;
import com.photolude.mob.user.dal.IUserAccessLayer;
import com.photolude.mob.user.dal.UserLogonData;

public class UserAccountDomain implements IUserAccountDomain {
	private int timeoutMinutes = 5;
	public int getTimeoutMinutes(){ return this.timeoutMinutes; }
	public UserAccountDomain setTimeoutMinutes(int value)
	{
		this.timeoutMinutes = value;
		return this;
	}
	
	private ILogonAccessLayer logonAccessLayer;
	public ILogonAccessLayer getLogonAccessLayer(){ return this.logonAccessLayer; }
	public UserAccountDomain setLogonAccessLayer(ILogonAccessLayer value)
	{
		this.logonAccessLayer = value;
		return this;
	}
	
	private IUserAccessLayer userAccessLayer;
	public IUserAccessLayer getUserAccessLayer(){ return this.userAccessLayer; }
	public UserAccountDomain setUserAccessLayer(IUserAccessLayer value)
	{
		this.userAccessLayer = value;
		return this;
	}
	
	@Override
	public String logon(String username, String password) {
		if(username == null || password == null || username.length() == 0 || password.length() == 0)
		{
			// Exit due to bad arguments
			return null;
		}
		
		Long userStaticId = this.logonAccessLayer.attemptLogOn(username, password);
		String temporaryId = null;
		
		if(userStaticId != null)
		{
			// Generate tempararyId
			temporaryId = UUID.randomUUID().toString();
			
			// Set temporary timeout
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, this.timeoutMinutes);
			Date timeout = calendar.getTime();
			
			//
			// Register temporary id
			//
			if(!this.userAccessLayer.setTemporaryUserId(userStaticId, temporaryId, timeout))
			{
				temporaryId = null;
			}
		}
		return temporaryId;
	}

	@Override
	public void logoff(String temporaryId) {
		if(temporaryId == null || temporaryId.length() == 0) return;
		
		this.userAccessLayer.removeTemporaryUserId(temporaryId);
	}

	@Override
	public boolean isLoggedIn(String temporaryId) {
		if(temporaryId == null || temporaryId.length() == 0) return false;
		
		boolean retval = false;
		UserLogonData data = this.userAccessLayer.getLogonData(temporaryId);
		
		retval = data != null && data.getExpiration().after(Calendar.getInstance().getTime());
		
		return retval;
	}
	
	@Override
	public Long getStaticIdFromToken(String temporaryId) {
		if(!isTemporaryIdValid(temporaryId)) return null;
		
		return this.userAccessLayer.getUserIdFromToken(temporaryId);
	}
	
	@Override
	public Long getStaticIdFromEmail(String email) {
		if(email == null || email.length() == 0)
		{
			return null;
		}
		
		return this.userAccessLayer.getUserIdFromEmail(email);
	}
	
	private boolean isTemporaryIdValid(String temporaryId)
	{
		return temporaryId != null && temporaryId.length() > 0;
	}
}
