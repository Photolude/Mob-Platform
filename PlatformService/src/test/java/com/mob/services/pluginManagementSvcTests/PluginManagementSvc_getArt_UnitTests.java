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

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.plugin.domain.IPluginDomain;
import com.mob.services.PluginManagementSvc;

@RunWith(Parameterized.class)
public class PluginManagementSvc_getArt_UnitTests {
	private static final String USER_TOKEN_VALID = "token";
	private static final String ROLE_VALID = "role";
	private static final String PATH_VALID = "path";
	private static final PluginArt ART_VALID = new PluginArt();
	private static final int STATUS_OK = 200;
	private static final int STATUS_FAIL = 500;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			USER_TOKEN_VALID,
    			ROLE_VALID,
    			PATH_VALID,
    			ART_VALID,
    			STATUS_OK
    		},
    		{
    			// 1. No parameters passed in
    			null,
    			null,
    			null,
    			ART_VALID,
    			STATUS_OK
    		},
    		{
    			// 2. No art returned
    			USER_TOKEN_VALID,
    			ROLE_VALID,
    			PATH_VALID,
    			null,
    			STATUS_FAIL
    		},
		});
	}
	
	private PluginManagementSvc service = new PluginManagementSvc();
	private String userToken;
	private String artPath;
	private String role;
	private int expectedStatus;
	
	public PluginManagementSvc_getArt_UnitTests(String userToken, String role, String artPath, PluginArt getArtRetval, int expectedStatus)
	{
		this.userToken = userToken;
		this.artPath = artPath;
		this.role = role;
		this.expectedStatus = expectedStatus;
		
		IPluginDomain domain = mock(IPluginDomain.class);
		Mockito.when(domain.getArt(userToken, role, artPath)).thenReturn(getArtRetval);
		
		this.service.setDomain(domain);
	}
	
	@Test
	public void getArt() throws Exception
	{
		Response response = this.service.getArt(this.userToken, this.role, this.artPath);
		
		Assert.assertNotNull(response);
		Assert.assertEquals(this.expectedStatus, response.getStatus());
	}
}
