package com.mob.plugin.model;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import com.mob.commons.plugins.servicemodel.WebPage;
import com.mob.plugin.dal.IPluginAccessLayer;
import com.mob.plugin.domain.PluginDomain;
import com.mob.user.domain.IUserAccountDomain;

@RunWith(Parameterized.class)
public class PluginDomain_HealthUnitTests 
{
	private static final WebPage[] PAGES_VALID = new WebPage[]{new WebPage().setId(1).setName("HealthStatusPage")};
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() 
	{
        return Arrays.asList(new Object[][] 
		{
    		{
    			true,
    			PAGES_VALID,
    			true
    		},
    		{
    			false,
    			PAGES_VALID,
    			false
    		},
    		{
    			true,
    			null,
    			false
    		},
		});
	}
	
	private PluginDomain domain;
	private IPluginAccessLayer dataAccessLayer;
	private boolean expectedResult;

	
	public PluginDomain_HealthUnitTests(boolean canConnect, WebPage[] getPages, boolean expectedResult)
	{
		this.expectedResult = expectedResult;
		
		//
		// Setup DAL
		//
		
		this.dataAccessLayer = mock(IPluginAccessLayer.class);
		
		
		Mockito.when(this.dataAccessLayer.canConnect()).thenReturn(canConnect);
		Mockito.when(this.dataAccessLayer.getPages(1L)).thenReturn(getPages);
		
		//
		// Generate stub User Account Client
		//
		IUserAccountDomain accountDomain = mock(IUserAccountDomain.class);
		Mockito.when(accountDomain.getStaticIdFromEmail(anyString(), anyString())).thenReturn(1L);
		
		//
		// Create domain
		//
		this.domain = new PluginDomain()
						.setDataAccessLayer(this.dataAccessLayer)
						.setUserAccountService(accountDomain)
						.setDefaultPluginUser("");
	}
	
	@Test
	public void HealthyService()
	{
		boolean actualResult = this.domain.isHealthy();
		
		Assert.assertEquals(this.expectedResult, actualResult);
	}
}
