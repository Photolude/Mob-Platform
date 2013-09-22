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
public class PluginDomain_uninstallPluginForUser_UnitTests {

	private static final String[] REQUIRED_ROLES_NONE = new String[0];
	private static final PluginDefinition PLUGIN_VALID = new PluginDefinition().setRole("A Role");
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid case
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			true,
    			true
    		},
    		{
    			// 1. Invalid Plugin Id
    			ValueConstants.PLUGIN_ID_INVALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			true,
    			false
    		},
    		{
    			// 2. Invalid user token
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_INVALID,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			true,
    			false
    		},
    		{
    			// 3. Null user token
    			ValueConstants.PLUGIN_ID_VALID,
    			null,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			true,
    			false
    		},
    		{
    			// 4. Invalid Static Id
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_INVALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			true,
    			false
    		},
    		{
    			// 5. Failed call to uninstall plugin
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			REQUIRED_ROLES_NONE,
    			false,
    			false
    		},
			{
    			// 6. Plugin on list of required roles
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			PLUGIN_VALID,
    			new String[]{ PLUGIN_VALID.getRole() },
    			true,
    			false
    		},
    		{
    			// 7. Null plugin
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			null,
    			new String[]{ PLUGIN_VALID.getRole() },
    			true,
    			false
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private String userToken;
	private int pluginId;
	private boolean expectedResult;
	
	public PluginDomain_uninstallPluginForUser_UnitTests(int pluginId, String userToken, Long staticUserId, PluginDefinition plugin, String[] requiredRoles, boolean addPluginRetval, boolean expectedResult)
	{
		this.pluginId = pluginId;
		this.userToken = userToken;
		this.expectedResult = expectedResult;
		
		IPluginAccessLayer dal = mock(IPluginAccessLayer.class);
		Mockito.when(dal.removePluginFromUser(anyInt(), eq(pluginId))).thenReturn(addPluginRetval);
		Mockito.when(dal.getRequiredRoles()).thenReturn(requiredRoles);
		Mockito.when(dal.getPluginById(pluginId)).thenReturn(plugin);

		IUserAccountDomain uas = mock(IUserAccountDomain.class);
		Mockito.when(uas.getStaticIdFromToken(userToken)).thenReturn(staticUserId);

		this.domain.setDataAccessLayer(dal);
		this.domain.setUserAccountService(uas);
	}
	
	@Test
	public void uninstallPlugin()
	{
		boolean result = this.domain.uninstallPluginForUser(pluginId, userToken);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
