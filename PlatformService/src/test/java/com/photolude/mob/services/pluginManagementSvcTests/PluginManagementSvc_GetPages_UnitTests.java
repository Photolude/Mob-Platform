package com.photolude.mob.services.pluginManagementSvcTests;

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

import com.photolude.mob.commons.plugins.servicemodel.WebPage;
import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.services.PluginManagementSvc;


@RunWith(Parameterized.class)
public class PluginManagementSvc_GetPages_UnitTests {
	private static final String TOKEN_VALID = "Valid Token";
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			TOKEN_VALID,
    			new WebPage[]{new WebPage()},
    			PluginManagementSvc.STATUS_SUCCEEDED
    		},
    		{
    			// 1. Empty user
    			"",
    			new WebPage[]{new WebPage()},
    			PluginManagementSvc.STATUS_SUCCEEDED
    		},
    		{
    			// 2. Null user
    			null,
    			new WebPage[]{new WebPage()},
    			PluginManagementSvc.STATUS_SUCCEEDED
    		},
    		{
    			// 3. Empty page array
    			TOKEN_VALID,
    			new WebPage[]{},
    			PluginManagementSvc.STATUS_SUCCEEDED
    		},
    		{
    			// 4. Null page array
    			TOKEN_VALID,
    			null,
    			PluginManagementSvc.STATUS_BAD_REQUEST
    		},
		});
	}
	
	private String userToken;
	private PluginManagementSvc service;
	private WebPage[] pagePlugins;
	private int expectedStatus;
	
	public PluginManagementSvc_GetPages_UnitTests(String userToken, WebPage[] pagePlugins, int expectedStatus)
	{
		this.userToken = userToken;
		this.pagePlugins = pagePlugins;
		this.expectedStatus = expectedStatus;
		
		this.service = new PluginManagementSvc();
		
		IPluginDomain mockDomain = mock(IPluginDomain.class);
		Mockito.when(mockDomain.getPages(userToken)).thenReturn(pagePlugins);
		
		this.service.setDomain(mockDomain);
	}
	
	@Test
	public void TestGetPages()
	{
		Response response = this.service.getPages(userToken);
		Assert.assertEquals(this.expectedStatus, response.getStatus());
		Assert.assertEquals(this.pagePlugins, response.getEntity());
	}
}
