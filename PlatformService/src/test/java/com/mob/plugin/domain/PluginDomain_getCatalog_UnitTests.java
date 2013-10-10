package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;

import com.mob.commons.plugins.servicemodel.PluginCatalog;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.plugin.dal.IPluginAccessLayer;
import com.mob.plugin.domain.PluginDomain;
import com.mob.user.domain.IUserAccountDomain;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class PluginDomain_getCatalog_UnitTests {
	private static final String TOKEN_VALID = "this is a token";
	private static final Long USER_ID_VALID = 1L;
	private static final PluginDefinition[] PLUGINS_VALID = new PluginDefinition[]{new PluginDefinition()};
	private static final PluginCatalog CATALOG_VALID = new PluginCatalog().setPlugins(PLUGINS_VALID).setUserPlugins(PLUGINS_VALID);
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TOKEN_VALID,
    			USER_ID_VALID,
    			PLUGINS_VALID,
    			PLUGINS_VALID,
    			CATALOG_VALID
    		},
    		{
    			"",
    			USER_ID_VALID,
    			PLUGINS_VALID,
    			PLUGINS_VALID,
    			CATALOG_VALID
    		},
    		{
    			null,
    			USER_ID_VALID,
    			PLUGINS_VALID,
    			PLUGINS_VALID,
    			CATALOG_VALID
    		},
    		{
    			TOKEN_VALID,
    			null,
    			PLUGINS_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		{
    			TOKEN_VALID,
    			USER_ID_VALID,
    			null,
    			PLUGINS_VALID,
    			new PluginCatalog().setPlugins(PLUGINS_VALID)
    		},
    		{
    			TOKEN_VALID,
    			USER_ID_VALID,
    			PLUGINS_VALID,
    			null,
    			new PluginCatalog().setUserPlugins(PLUGINS_VALID)
    		},
    		{
    			TOKEN_VALID,
    			USER_ID_VALID,
    			null,
    			null,
    			new PluginCatalog()
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private IPluginAccessLayer pluginAccessLayer = mock(IPluginAccessLayer.class);
	private IUserAccountDomain userService = mock(IUserAccountDomain.class);
	private String token;
	private PluginCatalog expectedResult;
	
	public PluginDomain_getCatalog_UnitTests(String token, Long userId, PluginDefinition[] userPlugins, PluginDefinition[] fullPlugins, PluginCatalog expectedResult)
	{
		this.token = token;
		this.expectedResult = expectedResult;
		
		Mockito.when(this.userService.getStaticIdFromToken(token)).thenReturn(userId);
		
		Mockito.when(this.pluginAccessLayer.getUserPlugins((userId != null)? userId.longValue() : 0L)).thenReturn(userPlugins);
		Mockito.when(this.pluginAccessLayer.getPlugins(token)).thenReturn(fullPlugins);
		
		this.domain.setDataAccessLayer(this.pluginAccessLayer);
		this.domain.setUserAccountService(this.userService);
	}
	
	@Test
	public void getCatalogTest() throws Exception
	{
		PluginCatalog response = this.domain.getCatalog(this.token);
		
		Assert.assertEquals(this.expectedResult, response);
	}
}
