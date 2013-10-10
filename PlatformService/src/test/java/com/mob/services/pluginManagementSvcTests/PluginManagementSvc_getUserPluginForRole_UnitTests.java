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

import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.plugin.domain.IPluginDomain;
import com.mob.services.PluginManagementSvc;

@RunWith(Parameterized.class)
public class PluginManagementSvc_getUserPluginForRole_UnitTests {
	private static final PluginDefinition PLUGIN_DEFINITION_VALID = new PluginDefinition();
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			null,
    			null,
    			PLUGIN_DEFINITION_VALID,
    			PLUGIN_DEFINITION_VALID,
    			200
    		},
    		{
    			null,
    			null,
    			null,
    			null,
    			500
    		},
		});
	}
	
	private PluginManagementSvc service = new PluginManagementSvc();
	private String userToken;
	private String role;
	private int expectedStatusCode;
	private Object expectedEntity;
	
	public PluginManagementSvc_getUserPluginForRole_UnitTests(String userToken, String role, PluginDefinition entityResponse, Object expectedEntity, int expectedStatusCode)
	{
		this.userToken = userToken;
		this.role = role;
		this.expectedStatusCode = expectedStatusCode;
		this.expectedEntity = expectedEntity;
		
		IPluginDomain domain = mock(IPluginDomain.class);
		Mockito.when(domain.getPluginForRole(userToken, role)).thenReturn(entityResponse);
		
		this.service.setDomain(domain);
	}
	
	@Test
	public void getUserPluginForRole()
	{
		Response response = this.service.getUserPluginForRole(userToken, role);
		
		Assert.assertNotNull(response);
		Assert.assertEquals(this.expectedStatusCode, response.getStatus());
		Assert.assertEquals(this.expectedEntity, response.getEntity());
	}
}
