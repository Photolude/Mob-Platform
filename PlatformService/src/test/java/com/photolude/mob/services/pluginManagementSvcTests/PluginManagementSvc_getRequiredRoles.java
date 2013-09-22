package com.photolude.mob.services.pluginManagementSvcTests;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.services.PluginManagementSvc;

public class PluginManagementSvc_getRequiredRoles {
	private IPluginDomain domain = mock(IPluginDomain.class);
	private PluginManagementSvc service = new PluginManagementSvc();
	
	public PluginManagementSvc_getRequiredRoles()
	{
		this.service.setDomain(this.domain);
	}
	
	@Test
	public void getRequiredRoles_Valid()
	{
		String[] array = new String[]{"test"};
		Mockito.when(this.domain.getRequiredRoles()).thenReturn(array);
		
		Response response = this.service.getRequiredRoles();
		
		Assert.assertNotNull(response);
		Assert.assertEquals(200, response.getStatus());
		Assert.assertArrayEquals(array, (String[])response.getEntity());
	}
	
	@Test
	public void getRequiredRoles_Invalid()
	{
		Mockito.when(this.domain.getRequiredRoles()).thenReturn(null);

		Response response = this.service.getRequiredRoles();

		Assert.assertNotNull(response);
		Assert.assertEquals(500, response.getStatus());
	}
}
