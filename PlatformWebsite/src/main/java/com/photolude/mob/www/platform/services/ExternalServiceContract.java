package com.photolude.mob.www.platform.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.photolude.mob.commons.plugins.servicemodel.PluginDataCall;
import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.ServiceAlias;
import com.photolude.mob.www.platform.controller.PlatformController;
import com.photolude.mob.www.platform.model.DataCallResponse;

public class ExternalServiceContract implements IServiceContracts {
	private static final String SESSION_EXTERNALS = "externalservices";
	private static final int STATUS_OK = 200;
	
	public void setExternalServices(PluginPage page, HttpSession session)
	{
		Map<String,String> externalServices = new HashMap<String,String>();
		
		for(PluginDefinition plugin : page.getPlugins())
		{
			if(plugin.getServiceAliases() != null)
			{
				for(ServiceAlias service : plugin.getServiceAliases())
				{
					String key = plugin.getRole() + "/" + service.getName();
					if(!externalServices.containsKey(key))
					{
						externalServices.put(key, service.getEndpoint());
					}
				}
			}
		}
		
		session.setAttribute(SESSION_EXTERNALS, externalServices);
	}
	
	public boolean isCallAllowed(HttpSession session, String serviceCall)
	{
		String alias = serviceCall;
		int firstSlash = serviceCall.indexOf('/');
		if(firstSlash >= 0 && serviceCall.indexOf('/', firstSlash + 1) > 0)
		{
			int secondSlash = serviceCall.indexOf('/', firstSlash + 1);
			alias = serviceCall.substring(0, secondSlash);
		}
		
		Object sessionObject = session.getAttribute(SESSION_EXTERNALS);
		if(sessionObject instanceof Map)
		{
			@SuppressWarnings("unchecked")
			Map<String,String> externalServices = (Map<String,String>)sessionObject;
			
			if(externalServices != null)
			{
				return externalServices.containsKey(alias);
			}
		}
		
		return false;
	}

	@Override
	public HttpResponse callServiceWithGet(HttpSession session, String serviceCall) {
		Logger logger = Logger.getLogger(this.getClass());
		
		String alias = serviceCall;
		String params = null;
		int firstSlash = serviceCall.indexOf('/');
		if(firstSlash >= 0 && serviceCall.indexOf('/', firstSlash + 1) > 0)
		{
			int secondSlash = serviceCall.indexOf('/', firstSlash + 1);
			alias = serviceCall.substring(0, secondSlash);
			params = serviceCall.substring(secondSlash, serviceCall.length());
		}
		
		
		String endpoint = getEndpointFromAlias(session, alias);
		if(endpoint == null)
		{
			logger.error("Could not find the specified alias " + alias);
			return null;
		}
		
		if(params != null)
		{
			endpoint += params;
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpResponse response = null;
		try {
			response = client.execute(new HttpGet(endpoint));
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return response;
	}

	@Override
	public HttpResponse callServiceWithPost(HttpSession session, String alias, String data, String contentType) {
		Logger logger = Logger.getLogger(this.getClass());
		
		String endpoint = getEndpointFromAlias(session, alias);
		if(endpoint == null)
		{
			logger.error("Could not find the specified alias " + alias);
			return null;
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpResponse response = null;
		try {
			HttpPost postCommand = new HttpPost(endpoint);
			StringEntity entity = new StringEntity(data);
			entity.setContentType(contentType);
			postCommand.setEntity(entity);
			response = client.execute(postCommand);
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return response;
	}
	
	@Override
	public HttpResponse callServiceWithPut(HttpSession session, String alias, String data, String contentType) 
	{
		Logger logger = Logger.getLogger(this.getClass());
		
		String endpoint = getEndpointFromAlias(session, alias);
		if(endpoint == null)
		{
			logger.error("Could not find the specified alias " + alias);
			return null;
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpResponse response = null;
		try {
			HttpPut postCommand = new HttpPut(endpoint);
			StringEntity entity = new StringEntity(data);
			entity.setContentType(contentType);
			postCommand.setEntity(entity);
			response = client.execute(postCommand);
		} catch (URIException e) {
			logger.warn(e);
		} catch (HttpException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} catch (URISyntaxException e) {
			logger.warn(e);
		}
		
		return response;
	}

	@Override
	public DataCallResponse[] callDataCalls(PluginDataCall[] dataCallInfo, HttpSession session, String userToken) {
		List<DataCallResponse> retval = new LinkedList<DataCallResponse>();
		Logger logger = Logger.getLogger(this.getClass());
		
		if(dataCallInfo != null)
		{
			for(PluginDataCall call : dataCallInfo)
			{
				if(this.isCallAllowed(session, call.getUri()))
				{
					String method = call.getMethod().toLowerCase();
					String uri = call.getUri().replace("${usertoken}", userToken);
					HttpResponse response = null;
					if(method.equals("post"))
					{
						logger.debug("calling service with post" + uri + " , with data " + call.getContent());
						response = this.callServiceWithPost(session, uri, call.getContent(), call.getContentType());
					}
					else if(method.equals("get"))
					{
						logger.debug("calling service with get" + call.getUri());
						response = this.callServiceWithGet(session, call.getUri());
					}
					
					String data = "";
					if(response != null && response.getStatusLine().getStatusCode() == STATUS_OK)
					{
						InputStream serviceStream = null;
						try {
							serviceStream = response.getEntity().getContent();
							StringWriter writer = new StringWriter();
							IOUtils.copy(serviceStream, writer);
							data = writer.toString();
						} catch (IOException e) {
							logger.error("An error occured while reading the response stream, check the network connection");
							logger.info(e);
						} catch (IllegalStateException e1) {
							logger.error("An error occured while reading the response stream, check the network connection");
							logger.info(e1);
						}
						
						if(serviceStream != null)
						{
							try {
								serviceStream.close();
							} catch (IOException e) {
							}
						}
					}
					
					DataCallResponse item = new DataCallResponse()
													.setDataCallInfo(call)
													.setData(data);
					
					retval.add(item);
				}
			}
		}
		return retval.toArray(new DataCallResponse[retval.size()]);
	}
	
	private String getEndpointFromAlias(HttpSession session, String alias)
	{
		Object sessionObject = session.getAttribute(SESSION_EXTERNALS);
		if(!(sessionObject instanceof Map))
		{
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String,String> externalServices = (Map<String,String>)sessionObject;
		
		if(externalServices == null || !externalServices.containsKey(alias))
		{
			return null;
		}
		
		String retval = externalServices.get(alias);
		
		if(retval != null)
		{
			String token = (String)session.getAttribute(PlatformController.SESSION_USER_TOKEN);
			retval = retval.replace("${usertoken}", token);
		}
		
		return retval;
	}
}
