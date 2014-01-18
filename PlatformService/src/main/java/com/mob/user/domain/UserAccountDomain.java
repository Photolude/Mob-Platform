package com.mob.user.domain;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mob.user.dal.ILogonAccessLayer;
import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.dal.UserLogonData;
import com.mob.user.externallogin.ISourceLoginClient;
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
	
	private List<ISourceLoginClient> sourceLoginClients;
	private Map<String, ISourceLoginClient> loginClientsLookup;
	public List<ISourceLoginClient> getSourceLoginClients(){ return this.sourceLoginClients; }
	public UserAccountDomain setSourceLoginClients(List<ISourceLoginClient> value)
	{
		if(value != null)
		{
			this.loginClientsLookup = new HashMap<String, ISourceLoginClient>();
			for(ISourceLoginClient client : value)
			{
				if(!this.loginClientsLookup.containsKey(client.getSourceName()))
				{
					this.loginClientsLookup.put(client.getSourceName(), client);
				}
				else
				{
					throw new InvalidParameterException(String.format("The values provided has two clients which are responsible for the same source (%s)", client.getSourceName()));
				}
			}
		}
		this.sourceLoginClients = value;
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
		
		if(userStaticId == null)
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
			return null;
		}
		
		return temporaryId.toString();
	}
	
	@Override
	public String logonViaSource(String token, String source) {
		if(StringUtils.isNullOrEmpty(token))
		{
			return null;
		}
		
		ISourceLoginClient sourceClient = this.loginClientsLookup.get(source);
		if(sourceClient == null)
		{
			return null;
		}
		
		TemporaryId tempId = sourceClient.login(token);
		
		return (tempId != null)? tempId.toString() : null;
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
