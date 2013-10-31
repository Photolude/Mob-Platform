package com.mob.www.platform.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingAsyncClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.log4j.Logger;

import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.www.platform.model.DataCallResponse;
import com.mysql.jdbc.StringUtils;

public class ServiceCallManager implements IServiceCallManager {
	private static final int STATUS_OK = 200;
	private final Logger logger = Logger.getLogger(ServiceCallManager.class);
	private final Pattern tokenPattern = Pattern.compile("\\$\\{.*?\\}");

	private DefaultHttpAsyncClient httpClient = null;
	public DefaultHttpAsyncClient getHttpClient(){ return this.httpClient; }
	public ServiceCallManager setHttpClient(DefaultHttpAsyncClient value)
	{
		this.httpClient = value;
		return this;
	}

	private IServiceContracts serviceContracts;
	public IServiceContracts getServiceContracts(){ return this.serviceContracts; }
	public ServiceCallManager setServiceContracts(IServiceContracts value)
	{
		this.serviceContracts = value;
		return this;
	}

	public HttpResponse callServiceWithGet(String serviceCall, ServiceCallContext context) 
	{
		HttpResponse retval = null;
		
		try {
			Future<HttpResponse> response = this.callServiceWithGetAsync(serviceCall, context);
			
			if(response != null)
			{
				retval = response.get();
			}
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (ExecutionException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	public Future<HttpResponse> callServiceWithGetAsync(String alias, ServiceCallContext context) {
		if(alias == null || alias.length() == 0)
		{
			logger.warn("Service call was null or empty");
			return null;
		}
		
		String endpoint = getEndpointFromAlias(alias, context);
		if(StringUtils.isNullOrEmpty(endpoint))
		{
			logger.warn("Could not find the specified alias " + alias);
			return null;
		}
		
		this.getConnection();
		
		Future<HttpResponse> response = null;
		response = this.httpClient.execute(new HttpGet(endpoint), null);
		
		return response;
	}

	@Override
	public HttpResponse callServiceWithPost(String alias, String data, String contentType, ServiceCallContext context) {
		HttpResponse retval = null;
		
		try {
			Future<HttpResponse> response = this.callServiceWithPostAsync(alias, data, contentType, context);
			
			if(response != null)
			{
				retval = response.get();
			}
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (ExecutionException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	public Future<HttpResponse> callServiceWithPostAsync(String alias, String data, String contentType, ServiceCallContext context)
	{
		if(alias == null || alias.length() == 0)
		{
			logger.warn("Service call was null or empty");
			return null;
		}
		
		String endpoint = getEndpointFromAlias(alias, context);
		if(StringUtils.isNullOrEmpty(endpoint))
		{
			logger.warn("Could not find the specified alias " + alias);
			return null;
		}
		
		String formattedData = constructData(data, context.getTokenLookup());
		if(formattedData == null && data != null)
		{
			logger.warn("Not making the service call because the data is invalid. " + alias);
			return null;
		}
		
		this.getConnection();
		Future<HttpResponse> response = null;
		try {
			HttpPost postCommand = new HttpPost(endpoint);
			if(!StringUtils.isNullOrEmpty(data))
			{
				StringEntity entity = new StringEntity(data);
				entity.setContentType(contentType);
				postCommand.setEntity(entity);
			}
			
			response = this.httpClient.execute(postCommand, null);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return response;
	}
	
	@Override
	public HttpResponse callServiceWithPut(String alias, String data, String contentType, ServiceCallContext context)
	{
		HttpResponse retval = null;
		
		try {
			Future<HttpResponse> response = this.callServiceWithPutAsync(alias, data, contentType, context);
			
			if(response != null)
			{
				retval = response.get();
			}
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (ExecutionException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	public Future<HttpResponse> callServiceWithPutAsync(String alias, String data, String contentType, ServiceCallContext context) 
	{
		if(alias == null || alias.length() == 0)
		{
			logger.warn("Service call was null or empty");
			return null;
		}
		
		String endpoint = getEndpointFromAlias(alias, context);
		if(StringUtils.isNullOrEmpty(endpoint))
		{
			logger.warn("Not making the service call because it either could not be found or had parameters missing. " + alias);
			return null;
		}
		
		String formattedData = constructData(data, context.getTokenLookup());
		if(formattedData == null && data != null)
		{
			logger.warn("Not making the service call because the data is invalid. " + alias);
			return null;
		}
		
		this.getConnection();
		Future<HttpResponse> response = null;
		try {
			HttpPut postCommand = new HttpPut(endpoint);
			if(!StringUtils.isNullOrEmpty(data))
			{
				StringEntity entity = new StringEntity(data);
				entity.setContentType(contentType);
				postCommand.setEntity(entity);
			}
			response = this.httpClient.execute(postCommand, null);
		} catch (IOException e) {
			logger.warn(e);
		}
		
		return response;
	}
	
	@Override
	public HttpResponse callServiceWithDelete(String alias, String data, String contentType, ServiceCallContext context)
	{
		HttpResponse retval = null;
		
		try {
			Future<HttpResponse> response = this.callServiceWithDeleteAsync(alias, data, contentType, context);

			if(response != null)
			{
				retval = response.get();
			}
		} catch (InterruptedException e) {
			logger.warn(e);
		} catch (ExecutionException e) {
			logger.warn(e);
		}
		
		return retval;
	}
	
	public Future<HttpResponse> callServiceWithDeleteAsync(String alias, String data, String contentType, ServiceCallContext context) 
	{
		if(alias == null || alias.length() == 0)
		{
			logger.warn("Service call was null or empty");
			return null;
		}
		
		String endpoint = getEndpointFromAlias(alias, context);
		if(StringUtils.isNullOrEmpty(endpoint))
		{
			logger.warn("Could not find the specified alias " + alias);
			return null;
		}

		String formattedData = constructData(data, context.getTokenLookup());
		if(formattedData == null && data != null)
		{
			logger.warn("Not making the service call because the data is invalid. " + alias);
			return null;
		}
		
		this.getConnection();
		Future<HttpResponse> response = null;
		HttpDelete deleteCommand = new HttpDelete(endpoint);
		if(!StringUtils.isNullOrEmpty(data))
		{
			deleteCommand.setHeader("data", data);
		}
		response = this.httpClient.execute(deleteCommand, null);
		
		return response;
	}

	@Override
	public DataCallResponse[] callDataCalls(PluginDataCall[] dataCallInfo, ServiceCallContext context)
	{
		List<DataCallResponse> retval = new LinkedList<DataCallResponse>();
		
		if(dataCallInfo != null)
		{
			for(PluginDataCall call : dataCallInfo)
			{
				if(this.serviceContracts.isCallAllowed(call.getUri(), context))
				{
					String method = call.getMethod().toLowerCase();
					Future<HttpResponse> response = null;
					if(method.equals("post"))
					{
						logger.debug("calling service with post" + call.getUri() + " , with data " + call.getContent());
						response = this.callServiceWithPostAsync(call.getUri(), call.getContent(), call.getContentType(), context);
					}
					else if(method.equals("get"))
					{
						logger.debug("calling service with get" + call.getUri());
						response = this.callServiceWithGetAsync(call.getUri(), context);
					}
					else if(method.equals("put"))
					{
						logger.debug("calling service with put" + call.getUri());
						response = this.callServiceWithPutAsync(call.getUri(), call.getContent(), call.getContentType(), context);
					}
					else if(method.equals("delete"))
					{
						logger.debug("calling service with delete" + call.getUri());
						response = this.callServiceWithDeleteAsync(call.getUri(), call.getContent(), call.getContentType(), context);
					}
					
					DataCallResponse item = new DataCallResponse()
												.setDataCallInfo(call)
												.setResponse(response);

					retval.add(item);
				}
			}
			
			for(DataCallResponse item : retval)
			{
				String data = "";
				HttpResponse response = null;
				try {
					response = item.getResponse().get();
				} catch (InterruptedException e2) {
					logger.warn(e2);
				} catch (ExecutionException e2) {
					logger.warn(e2);
				}
				
				if(item.getResponse() != null && response.getStatusLine().getStatusCode() == STATUS_OK)
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
							logger.warn(e);
						}
					}
					
					item.setData(data);
				}
			}
		}
		return retval.toArray(new DataCallResponse[retval.size()]);
	}
	
	private String getEndpointFromAlias(String serviceCall, ServiceCallContext context)
	{
		String url = this.serviceContracts.getEndpointFromAlias(serviceCall, context);
		
		if(url == null)
		{
			return null; 
		}
		
		StringBuilder urlConstructor = new StringBuilder();
		urlConstructor.append(url);
		
		if(context.getExtendedPath() != null)
		{
			urlConstructor.append(context.getExtendedPath());
		}
		
		return constructData(urlConstructor.toString(), context.getTokenLookup());
	}
	
	private String constructData(String data, Map<String,String> tokenLookup)
	{
		if(StringUtils.isNullOrEmpty(data))
		{
			return data;
		}
		
		Matcher matcher = this.tokenPattern.matcher(data);
		
		while(matcher.find())
		{
			String token = data.substring(matcher.start(), matcher.end());
			String key = token.substring(2, token.length() - 1);
			
			String value = tokenLookup.get(key);
			if(value == null)
			{
				return null;
			}
			
			data = data.replace(token, value);
		}
		
		return data;
	}
	
	private void getConnection()
	{
		if(this.httpClient == null)
		{
			synchronized(this)
			{
				if(this.httpClient == null)
				{
					DefaultHttpAsyncClient client;
					try {
						DefaultConnectingIOReactor defaultioreactor = new DefaultConnectingIOReactor();
						PoolingAsyncClientConnectionManager pool = new PoolingAsyncClientConnectionManager(defaultioreactor);

						client = new DefaultHttpAsyncClient(pool);
						client.start();

						this.httpClient = client;
					} catch (IOReactorException e) {
						logger.error("Creating the http communication layer failed");
						logger.info(e);
					}
				}
			}
		}
	}
}
