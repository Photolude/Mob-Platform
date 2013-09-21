package com.photolude.mob.plugin.domain;

import org.junit.Assert;
import org.junit.Test;


import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.plugin.domain.PluginDomain;
import com.photolude.mob.user.domain.IUserAccountDomain;

public abstract class PluginDomain_getUserPagePlugins_LogicBase {
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

	private PluginDomain domain;
	private PluginDefinition[] expectedResult;
	private String userName;
	
	public PluginDomain_getUserPagePlugins_LogicBase(String userName, PluginDefinition[] rawData, PluginDefinition[] expectedResult)
	{
		IPluginAccessLayer mockDAL = mock(IPluginAccessLayer.class);
		Mockito.when(mockDAL.getUserPlugins(anyLong())).thenReturn(rawData);
		
		IUserAccountDomain userService = mock(IUserAccountDomain.class);
		Mockito.when(userService.getStaticIdFromToken(any(String.class))).thenReturn(1L);
		
		this.domain = new PluginDomain()
						.setDataAccessLayer(mockDAL)
						.setUserAccountService(userService);
		
		this.userName = userName;
		this.expectedResult = expectedResult;
	}
	
	@Test
	public void executeGetUserPlugins()
	{
		PluginDefinition[] actual = this.domain.getUserPlugins(this.userName);
		
		Assert.assertArrayEquals(this.expectedResult, actual);
	}
}
