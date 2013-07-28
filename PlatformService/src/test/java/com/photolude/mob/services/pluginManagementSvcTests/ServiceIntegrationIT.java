package com.photolude.mob.services.pluginManagementSvcTests;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServiceIntegrationIT {
	private String urlEndpoint;
	private HttpClient client;
	
	@Before
	public void Setup()
	{
		this.client = new DefaultHttpClient();
		this.urlEndpoint = System.getProperty("service.url");
	}

	@Test
	public void HeathTest() throws Exception
	{
		this.callEndpoint(this.urlEndpoint + "Plugins/Status/Health");
	}
	
	@Test
	public void LogonTest() throws Exception
	{
		this.callEndpoint(this.urlEndpoint + "user/logon/testuser@photolude.com/password");
	}
	
	@Test
	public void LogonOff() throws Exception
	{
		this.callEndpoint(this.urlEndpoint + "user/tokendata/logoff");
	}
	
	private void callEndpoint(String endpoint) throws Exception
	{
		Logger logger = Logger.getLogger(this.getClass());
		logger.info(endpoint);
		HttpResponse response = this.client.execute(new HttpGet(endpoint));
		Assert.assertTrue("The status code was not expected (" + response.getStatusLine().getStatusCode() + ")", response.getStatusLine().getStatusCode() == 200);
	}
}
