package com.mob.user.externallogin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.gson.Gson;
import com.mob.user.dal.IUserAccessLayer;
import com.mob.user.domain.TemporaryId;

public class GoogleLoginClient implements ISourceLoginClient {
	private static final String USER_SOURCE = "google";
	private static String REDIRECT_URI = "http://localhost:13433";
	private static final List<String> SCOPES = Arrays.asList(
			com.google.api.services.oauth2.Oauth2Scopes.PLUS_LOGIN,
			com.google.api.services.oauth2.Oauth2Scopes.USERINFO_EMAIL);
	
	private static final Logger logger = Logger.getLogger(GoogleLoginClient.class);
	private static final HttpTransport httpTransport = new NetHttpTransport();
	private static final JacksonFactory jsonFactory = new JacksonFactory();
	
	
	@Override
	public String getSourceName(){ return USER_SOURCE; }
	
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
	
	@Override
	public TemporaryId login(String token)
	{
		Userinfo userInfo = null;
		GoogleTokenResponse response = null;
		try {
			//
			// Do google user validation
			//
			
			response = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory, this.clientId, this.clientSecret, token, "postmessage")
			.setCode(token)
			.setScopes(SCOPES)
			.execute();
			
			GoogleCredential credentials = new GoogleCredential().setFromTokenResponse(response);
			
			Oauth2 userInfoService = new Oauth2.Builder(httpTransport, jsonFactory, credentials).build();
		    Tokeninfo tokenInfo = userInfoService.tokeninfo().setAccessToken(credentials.getAccessToken()).execute();
			if(tokenInfo.containsKey("error"))
			{
				logger.warn("token retrieved has an error");
				return null;
			}
		    
			userInfo = userInfoService.userinfo().get().execute();
		    
		} catch (IOException e) {
			logger.warn(e);
			return null;
		}
		
		//
		// Construct temporary id
		//
		Calendar expiration = Calendar.getInstance();
		expiration.add(Calendar.SECOND, response.getExpiresInSeconds().intValue());
		
		TemporaryId temporaryId = new TemporaryId(USER_SOURCE, expiration.getTime());
		
		//
		// Check for user's existance
		//
		Long staticUserId = this.userAccessLayer.getUserIdFromEmail(userInfo.getEmail(), USER_SOURCE);
		
		String sourceBlob = null;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			sourceBlob = mapper.writeValueAsString(response);
		} catch (IOException e) {
			logger.warn(e);
			return null;
		}
		
		if(staticUserId != null)
		{
			// User exists so set the data
			if(!this.userAccessLayer.setTemporaryUserId(staticUserId, temporaryId.toString(), expiration.getTime(), sourceBlob))
			{
				return null;
			}
		}
		else
		{
			// User does not exist in mob, time to create one
			if(!this.userAccessLayer.addUser(userInfo.getEmail(), USER_SOURCE, temporaryId.toString(), expiration.getTime(), sourceBlob))
			{
				return null;
			}
		}
		
		return temporaryId;
	}
	
}
