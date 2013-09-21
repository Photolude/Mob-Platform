package com.photolude.mob.plugin.model;


import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;
import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.services.PluginManagementSvc;


public abstract class ServiceLogicBase {
	protected static final String INVALID_USER_ID = "This is invalid..............................................................................................";
	protected static final String VALID_USER_ID = "This is valid";
	protected static final String INVALID_PAGE_NAME = "This is invalid..............................................................................................";
	protected static final String VALID_PAGE_NAME = "This is valid";
	protected static final String PLUGIN_NAME = "Default Plugin Name";
	protected static final String COMPANY_DEFAULT = "Company X";
	protected static final String COMPANY_2 = "Company X";
	protected static final String VERSION_1_0 = "1.0";
	protected static final String VERSION_2_0 = "2.0";
	protected static final String DEFAULT_SCRIPT = "Script";
	
	protected static final PluginDefinition[] PLUGINS_VALID = new PluginDefinition[]{ new PluginDefinition().setCompany(COMPANY_DEFAULT).setVersion(VERSION_1_0).setRole("Test Role")};
	protected static final PluginScript[] SCRIPTS_VALID = new PluginScript[]{ new PluginScript().setType("HTML").setScript("<div></div>") };
	
	protected static final PluginPage PAGE_VALID = new PluginPage()
													.setScripts(SCRIPTS_VALID)
													.setPlugins(PLUGINS_VALID);

	private PluginManagementSvc service;
	private int validStatusCode;
	private PluginPage expectedResult;
	private String userName;
	private String pageName;
	
	public ServiceLogicBase(String userName, String pageName, PluginPage rawData, PluginPage expectedResult, int validStatusCode)
	{
		IPluginDomain mockDomain = mock(IPluginDomain.class);
		Mockito.when(mockDomain.getPagePlugins(userName, pageName)).thenReturn(rawData);
		
		this.service = new PluginManagementSvc()
						.setDomain(mockDomain);
		
		this.userName = userName;
		this.pageName = pageName;
		this.expectedResult = expectedResult;
		this.validStatusCode = validStatusCode;
	}
	
	@Test
	public void executeGetUserPlugins() throws Exception
	{
		Response response = this.service.getUserPagePlugins(this.userName, this.pageName);
		
		Assert.assertNotNull("Response is null", response);
		
		Assert.assertTrue("Status is not expected (expected: " + this.validStatusCode + ", actual: " + response.getStatus() + " )",
				response.getStatus() == this.validStatusCode);
		
		Assert.assertTrue("Has entity is not expected (expected: " + (this.validStatusCode == PluginManagementSvc.STATUS_SUCCEEDED) + ", actual: " + response.hasEntity() + ")", 
				response.hasEntity() == (this.validStatusCode == PluginManagementSvc.STATUS_SUCCEEDED));
		
		if(response.hasEntity())
		{
			Object entityObject = response.getEntity();
			Assert.assertNotNull(entityObject);
			
			PluginPage entity = (PluginPage)response.getEntity();
			
			Assert.assertArrayEquals(this.expectedResult.getPlugins(), entity.getPlugins());
			Assert.assertArrayEquals(this.expectedResult.getScripts(), entity.getScripts());
		}
	}
}
