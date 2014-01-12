package com.mob.commons.service.clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;

public class DefaultUserServiceClient implements IUserServiceClient {
	private static final int STATUS_OK = 200;
	
	private String endpoint;
	public String getEndpoint(){ return this.endpoint; }
	public DefaultUserServiceClient setEndpoint(String value) throws URISyntaxException
	{
		this.endpoint = EndpointUtils.ensureEndpointWellFormed(value);
		return this;
	}
	@Override
	public String logon(String email, String password) {
		String retval = null;
		Logger logger = Logger.getLogger(this.getClass());
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		try {
			HttpResponse response = client.execute(new HttpGet(this.endpoint + "logon/mob/" + URIUtil.encodeAll(email) + "/" + URIUtil.encodeAll(password)));
			
			if(response.getStatusLine().getStatusCode() == STATUS_OK)
			{
				// Copy output to retval
				InputStream content = response.getEntity().getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(content, writer);
				retval = writer.toString();
				
				content.close();
			}
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	@Override
	public String logonViaGoogle(String token) {
		String retval = null;
		Logger logger = Logger.getLogger(this.getClass());
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		try {
			HttpResponse response = client.execute(new HttpGet(this.endpoint + "logon/google/" + URIUtil.encodeAll(token)));
			
			if(response.getStatusLine().getStatusCode() == STATUS_OK)
			{
				// Copy output to retval
				InputStream content = response.getEntity().getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(content, writer);
				retval = writer.toString();
				
				content.close();
			}
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	@Override
	public void logout(String userToken) {
		Logger logger = Logger.getLogger(this.getClass());
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			String endpointCall = this.endpoint + userToken + "/logout";
			HttpResponse response = client.execute(new HttpGet(endpointCall));

			if(response.getStatusLine().getStatusCode() != STATUS_OK)
			{
				logger.warn("the request to logoff failed for user " + endpointCall);
			}
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
	}
	
	@Override
	public Long getStaticIdFromToken(String token) {
		Logger logger = Logger.getLogger(this.getClass());
		Long retval = null;
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpGet request;
		try {
			request = new HttpGet(this.endpoint + "user/" + token + "/staticid");
			HttpResponse response = client.execute(request);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				// Copy output to retval
				InputStream content = response.getEntity().getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(content, writer);
				String data = writer.toString();
				
				retval = Long.parseLong(data, 10);
				
				content.close();
			}
			else
			{
				logger.warn("The http request failed with status of " + response.getStatusLine().getStatusCode());
				return null;
			}
		} catch (URISyntaxException e) {
			logger.warn(e);
			return null;
		} catch (IOException e) {
			logger.warn(e);
			return null;
		} catch (HttpException e) {
			logger.warn(e);
			return null;
		}
		
		return retval;
	}
}
