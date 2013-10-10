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

import com.mob.plugin.domain.IPluginDomain;
import com.mob.services.PluginManagementSvc;


@RunWith(Parameterized.class)
public class PluginManagementSvc_uninstallPluginForUser_UnitTests {
	private static final int PLUGIN_ID_VALID = 1;
	private static final String USER_TOKEN_VALID = "ValidToken";
	private static final int STATUS_OK = 200;
	private static final int STATUS_FAIL = 500;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 1. valid install
    			PLUGIN_ID_VALID,
    			USER_TOKEN_VALID, 
    			true,
    			STATUS_OK
    		},
    		{
    			// 1. invalid token id
    			-1,
    			USER_TOKEN_VALID,
    			true,
    			STATUS_OK
    		},
    		{
    			// 2. invalid userToken
    			PLUGIN_ID_VALID,
    			"",
    			true,
    			STATUS_OK
    		},
    		{
    			// 3. install failed
    			PLUGIN_ID_VALID,
    			USER_TOKEN_VALID,
    			false,
    			STATUS_FAIL
    		},
		});
	}
	
	private PluginManagementSvc service = new PluginManagementSvc();
	private int expectedStatus;
	private int pluginId;
	private String userToken;
	
	public PluginManagementSvc_uninstallPluginForUser_UnitTests(int pluginId, String userToken, boolean domainRetval, int expectedStatus)
	{
		this.expectedStatus = expectedStatus;
		this.pluginId = pluginId;
		this.userToken = userToken;
		
		IPluginDomain domain = mock(IPluginDomain.class);
		Mockito.when(domain.uninstallPluginForUser(pluginId, userToken)).thenReturn(domainRetval);
		
		this.service.setDomain(domain);
		
	}
	
	@Test
	public void uninstallPluginForUser()
	{
		Response response = this.service.uninstallPluginForUser(this.pluginId, this.userToken);
		
		Assert.assertEquals(this.expectedStatus, response.getStatus());
	}
}
