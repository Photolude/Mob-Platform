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

import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.services.PluginManagementSvc;

@RunWith(Parameterized.class)
public class ServiceHealthUnitTests 
{
	private static final int STATUS_OK = 200;
	private static final int STATUS_INTERNAL_ERROR = 500;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() 
	{
        return Arrays.asList(new Object[][] 
		{
    		{
    			true,
    			STATUS_OK
    		},
    		{
    			false,
    			STATUS_INTERNAL_ERROR
    		},
		});
	}
	
	private PluginManagementSvc service;
	private int status;

	
	public ServiceHealthUnitTests(boolean domainResult, int status)
	{
		IPluginDomain mockDomain = mock(IPluginDomain.class);
		Mockito.when(mockDomain.isHealthy()).thenReturn(domainResult);
		
		this.service = new PluginManagementSvc()
						.setDomain(mockDomain);
		
		this.status = status;
	}
	
	@Test
	public void HealthyService()
	{
		Response response = this.service.HealthStatus();
		
		Assert.assertTrue("Service responce not expected (expected: " + this.status + ", actual: " + response.getStatus() + ")", 
				response.getStatus() == this.status);
	}
}
