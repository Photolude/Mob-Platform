package com.mob.commons.service.clients;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.util.UriUtils;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginPage;

public class DefaultPluginServiceClient implements IPluginService {
	private String endpoint;
	public String getEndpoint(){ return this.endpoint; }
	public DefaultPluginServiceClient setEndpoint(String value)
	{
		if(!value.endsWith("/"))
		{
			value += "/";
		}
		this.endpoint = value;
		return this;
	}

	@Override
	public MainMenuItem[] getUserMenu(String userToken) {
		MainMenuItem[] retval = null;
		HttpClient client = new DefaultHttpClient();
		Logger logger = Logger.getLogger(this.getClass());
		
		try {
			HttpGet get = new HttpGet(this.endpoint + "user/" + userToken + "/menu");
			
			HttpResponse response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				InputStream stream = response.getEntity().getContent();
				retval = mapper.readValue(stream, MainMenuItem[].class);
				stream.close();
			}
		} catch (URISyntaxException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	@Override
	public PluginPage getPagePlugins(String userToken, String page)
	{
		PluginPage retval = null;
		HttpClient client = new DefaultHttpClient();
		Logger logger = Logger.getLogger(this.getClass());
		
		try {
			String requestPath = UriUtils.encodePath(this.endpoint + "user/" + userToken + "/page/" + page + "/get", "UTF-8");
			HttpGet get = new HttpGet(requestPath);
			
			HttpResponse response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				InputStream stream = response.getEntity().getContent();
				retval = mapper.readValue(stream, PluginPage.class);
				stream.close();
			}
		} catch (URISyntaxException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	@Override
	public PluginArt getArt(String userToken, String role, String artPath) {
		PluginArt retval = null;
		HttpClient client = new DefaultHttpClient();
		Logger logger = Logger.getLogger(this.getClass());
		
		try {
			String requestPath = UriUtils.encodePath(this.endpoint + "user/" + userToken + "/art/" + role + "/" + artPath, "UTF-8");
			HttpGet get = new HttpGet(requestPath);
			
			HttpResponse response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				InputStream stream = response.getEntity().getContent();
				retval = mapper.readValue(stream, PluginArt.class);
				stream.close();
			}
		} catch (URISyntaxException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	@Override
	public PluginDefinition getPluginForUserRole(String userToken, String role) {
		PluginDefinition retval = null;
		HttpClient client = new DefaultHttpClient();
		Logger logger = Logger.getLogger(this.getClass());
		
		try {
			String requestPath = UriUtils.encodePath(this.endpoint + "user/" + userToken + "/role/" + role, "UTF-8");
			HttpGet get = new HttpGet(requestPath);
			
			HttpResponse response = client.execute(get);
			
			if(response.getStatusLine().getStatusCode() == 200)
			{
				ObjectMapper mapper = new ObjectMapper();
				InputStream stream = response.getEntity().getContent();
				retval = mapper.readValue(stream, PluginDefinition.class);
				stream.close();
			}
		} catch (URISyntaxException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return retval;
	}

}
