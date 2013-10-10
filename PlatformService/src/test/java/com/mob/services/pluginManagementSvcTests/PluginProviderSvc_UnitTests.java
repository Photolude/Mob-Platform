package com.mob.services.pluginManagementSvcTests;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.plugin.domain.IPluginDomain;
import com.mob.services.PluginManagementSvc;

@RunWith(Parameterized.class)
public class PluginProviderSvc_UnitTests {
	
	private static final int RESPONSE_OK = 200;
	private static final int RESPONSE_FAIL = 400;
	
	private static final String PAGE_VALID = "This is a page";
	private static final String USER_TOKEN_VALID = "This is a user token";
	
	private static final PluginPage PAGE_DATA_VALID = new PluginPage();
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{

    		{
    			// 0. Valid javascript
    			USER_TOKEN_VALID,
    			PAGE_VALID,
    			PAGE_DATA_VALID,
    			PAGE_DATA_VALID,
    			RESPONSE_OK
    		},
    		{
    			// 1. Multiple javascripts
    			USER_TOKEN_VALID,
    			PAGE_VALID,
    			PAGE_DATA_VALID,
    			PAGE_DATA_VALID,
    			RESPONSE_OK
    		},
    		{
    			// 2. Null page
    			USER_TOKEN_VALID,
    			null,
    			PAGE_DATA_VALID,
    			PAGE_DATA_VALID,
    			RESPONSE_OK
    		},
    		{
    			// 3. Empty string page
    			USER_TOKEN_VALID,
    			"",
    			PAGE_DATA_VALID,
    			PAGE_DATA_VALID,
    			RESPONSE_OK
    		},
    		{
    			// 0. Valid javascript
    			USER_TOKEN_VALID,
    			PAGE_VALID,
    			null,
    			null,
    			RESPONSE_FAIL
    		},
		});
	}
	
	private PluginManagementSvc service;
	private String page;
	private String userToken;
	private PluginPage expectedResult;
	private int expectedStatus;
	private IPluginDomain domain;
	
	public PluginProviderSvc_UnitTests(String userToken, String page, PluginPage domainRetval, PluginPage expectedResult, int expectedStatus)
	{
		this.page = page;
		this.expectedResult = expectedResult;
		this.userToken = userToken;
		this.expectedStatus = expectedStatus;
		
		this.domain = mock(IPluginDomain.class);
		Mockito.when(this.domain.getPagePlugins(userToken, page)).thenReturn(domainRetval);
		
		this.service = new PluginManagementSvc()
						.setDomain(this.domain);
	}
	
	@Test
	public void getUserPagePluginsTests() throws Exception
	{
		Response actual = this.service.getUserPagePlugins(this.userToken, this.page);
		
		Assert.assertEquals(this.expectedStatus, actual.getStatus());
		verify(this.domain, times(1)).getPagePlugins(this.userToken, this.page);
		
		Assert.assertEquals(this.expectedResult, (PluginPage)actual.getEntity());
	}
}
