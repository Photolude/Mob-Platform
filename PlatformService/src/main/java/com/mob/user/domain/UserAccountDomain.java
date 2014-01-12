package com.mob.user.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import com.mob.user.dal.ILogonAccessLayer;
import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.dal.UserLogonData;
import com.mysql.jdbc.StringUtils;

public class UserAccountDomain implements IUserAccountDomain {
	private static final String USER_SOURCE = "Custom";
	
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
	
	private boolean allowGoogleLogon;
	public boolean getAllowGoogleLogon(){ return this.allowGoogleLogon; }
	public UserAccountDomain setAllowGoogleLogon(boolean value)
	{
		this.allowGoogleLogon = value;
		return this;
	}
	
	@Override
	public String logon(String username, String password) {
		if(StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password))
		{
			// Exit due to bad arguments
			return null;
		}
		
		Long userStaticId = this.logonAccessLayer.attemptLogOn(username, password);
		
		if(userStaticId != null)
		{
			return null;
		}

		// Set temporary timeout
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, this.timeoutMinutes);
		Date timeout = calendar.getTime();
		
		// Create temporary id
		Calendar expiration = Calendar.getInstance();
		expiration.add(Calendar.HOUR, 2);
		TemporaryId temporaryId = new TemporaryId(USER_SOURCE, timeout);
		
		//
		// Register temporary id
		//
		if(!this.userAccessLayer.setTemporaryUserId(userStaticId, temporaryId.toString(), timeout, null))
		{
			temporaryId = null;
		}
		
		return temporaryId.toString();
	}
	
	@Override
	public String logonViaGoogle(String token) {
		if(StringUtils.isNullOrEmpty(token) || !this.allowGoogleLogon)
		{
			return null;
		}
		
		
		
		return null;
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
	public Long getStaticIdFromEmail(String email, String source) {
		if(StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(source))
		{
			return null;
		}
		
		return this.userAccessLayer.getUserIdFromEmail(email, source);
	}
	
	private boolean isTemporaryIdValid(String temporaryId)
	{
		return temporaryId != null && temporaryId.length() > 0;
	}
}
