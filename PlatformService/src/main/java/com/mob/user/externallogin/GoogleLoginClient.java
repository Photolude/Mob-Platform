package com.mob.user.externallogin;

import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;
import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.domain.TemporaryId;

public class GoogleLoginClient implements IGoogleLoginClient {
	private static final String USER_SOURCE = "Google+";
	
	private static final Logger logger = Logger.getLogger(GoogleLoginClient.class);
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static final Gson GSON = new Gson();
	private static GoogleClientSecrets clientSecrets;
	
	private String clientId;
	public String getClientId(){ return this.clientId; }
	public GoogleLoginClient setClientId(String value)
	{
		this.clientId = value;
		return this;
	}
	
	private String clientSecret;
	public String getClientSecret(){ return this.clientSecret; }
	public GoogleLoginClient setClientSecret(String value)
	{
		this.clientSecret = value;
		return this;
	}
	
	private IUserAccessLayer userAccessLayer;
	public IUserAccessLayer getUserAccessLayer(){ return this.userAccessLayer; }
	public GoogleLoginClient setUserAccessLayer(IUserAccessLayer value)
	{
		this.userAccessLayer = value;
		return this;
	}
	
	public TemporaryId Login(String token)
	{
		GoogleTokenResponse tokenResponse = null;
		GoogleIdToken idToken = null;
		try {
			//
			// Do google user validation
			//
			tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY, this.clientId, this.clientSecret, token, "postmessage").execute();
			idToken = tokenResponse.parseIdToken();
		} catch (IOException e) {
			logger.warn(e);
			return null;
		}
		
		//
		// Construct temporary id
		//
		Calendar expiration = Calendar.getInstance();
		expiration.add(Calendar.SECOND, tokenResponse.getExpiresInSeconds().intValue());
		
		TemporaryId temporaryId = new TemporaryId(USER_SOURCE, expiration.getTime());
		
		//
		// Check for user's existance
		//
		Long staticUserId = this.userAccessLayer.getUserIdFromEmail(idToken.getPayload().getEmail(), USER_SOURCE);
		
		if(staticUserId != null)
		{
			// User exists so set the data
			this.userAccessLayer.setTemporaryUserId(staticUserId, temporaryId.toString(), expiration.getTime(), tokenResponse.getIdToken());
		}
		else
		{
			// User does not exist in mob, time to create one
			this.userAccessLayer.addUser(idToken.getPayload().getEmail(), USER_SOURCE, temporaryId.toString(), expiration.getTime(), tokenResponse.getIdToken());
		}
		
		return temporaryId;
	}
	
}
