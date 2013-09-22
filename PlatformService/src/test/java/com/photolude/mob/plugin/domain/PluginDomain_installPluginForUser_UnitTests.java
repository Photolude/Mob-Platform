package com.photolude.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.user.domain.IUserAccountDomain;

@RunWith(Parameterized.class)
public class PluginDomain_installPluginForUser_UnitTests {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid case
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			true
    		},
    		{
    			// 1. Invalid Plugin Id
    			ValueConstants.PLUGIN_ID_INVALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			false
    		},
    		{
    			// 2. Invalid user token
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_INVALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			false
    		},
    		{
    			// 3. Null user token
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			null,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			false
    		},
    		{
    			// 4. Invalid Static Id
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_INVALID,
    			true,
    			false,
    			false
    		},
    		{
    			// 5. Bad response from insert into database
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			false,
    			false,
    			false
    		},
    		{
    			// 6. No plugin returned
    			ValueConstants.PLUGIN_ID_VALID,
    			null,
    			ValueConstants.PLUGINS_NONE,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			false
    		},
    		{
    			// 7. A plugin already exists with that role
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			new PluginDefinition[]{ValueConstants.PLUGIN_DEBUG},
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			true,
    			true
    		},

    		{
    			// 8. User has no plugins
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.PLUGIN_DEBUG,
    			null,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false,
    			true
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private String userToken;
	private int pluginId;
	private boolean expectedResult;
	private boolean uninstallCalled;
	private IPluginAccessLayer dal;
	
	public PluginDomain_installPluginForUser_UnitTests(int pluginId, PluginDefinition plugin, PluginDefinition[] userPlugins, String userToken, Long staticUserId, boolean addPluginRetval, boolean uninstallCalled, boolean expectedResult)
	{
		this.pluginId = pluginId;
		this.userToken = userToken;
		this.expectedResult = expectedResult;
		this.uninstallCalled = uninstallCalled;
		
		this.dal = mock(IPluginAccessLayer.class);
		Mockito.when(this.dal.addPluginToUser(anyInt(), eq(pluginId))).thenReturn(addPluginRetval);
		Mockito.when(this.dal.getPluginById(pluginId)).thenReturn(plugin);
		Mockito.when(this.dal.getUserPlugins(anyLong())).thenReturn(userPlugins);
		
		IUserAccountDomain uas = mock(IUserAccountDomain.class);
		Mockito.when(uas.getStaticIdFromToken(userToken)).thenReturn(staticUserId);
		
		this.domain.setDataAccessLayer(this.dal);
		this.domain.setUserAccountService(uas);
	}
	
	@Test
	public void installPlugin()
	{
		boolean result = this.domain.installPluginForUser(pluginId, userToken);
		
		Assert.assertEquals(this.expectedResult, result);
		
		verify(this.dal, times((this.uninstallCalled)? 1 : 0)).removePluginFromUser(anyLong(), anyInt());
	}
}
