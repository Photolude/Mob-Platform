package com.mob.user.dal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mob.commons.service.clients.EndpointUtils;
import com.mob.commons.servicemodel.logon.LogonRequest;

public class DefaultLogonAccessLayer implements ILogonAccessLayer {
	
	private String endpoint;
	public String getEndpoint(){ return this.endpoint; }
	public DefaultLogonAccessLayer setEndpoint(String value) throws URISyntaxException
	{
		this.endpoint = EndpointUtils.ensureEndpointWellFormed(value);
		return this;
	}
	
	@Override
	public Long attemptLogOn(String username, String password) {
		Logger logger = Logger.getLogger(this.getClass());
		DefaultHttpClient client  = new DefaultHttpClient();
		Long retval = null;
		
		HttpPost logonRequest = new HttpPost(this.endpoint + "user/logon");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			StringEntity entity = new StringEntity(mapper.writeValueAsString(new LogonRequest().setUserName(username).setPassword(password)), HTTP.UTF_8);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			logonRequest.setEntity(entity);
			
			HttpResponse response = client.execute(logonRequest);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				// Copy output to retval
				InputStream content = response.getEntity().getContent();
				StringWriter writer = new StringWriter();
				IOUtils.copy(content, writer);
				retval = Long.parseLong(writer.toString());
				
				content.close();
			}
			else
			{
				logger.warn("Logon failed for user " + username);
			}
		} catch (JsonGenerationException e) {
			logger.error("contract serialization failed");
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error("contract serialization failed");
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error("contract serialization failed");
			logger.error(e);
		} catch (IOException e) {
			logger.error("contract serialization failed");
			logger.error(e);
		}
		
		return retval;
	}
}
