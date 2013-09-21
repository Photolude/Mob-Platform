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
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			true
    		},
    		{
    			// 1. Invalid Plugin Id
    			ValueConstants.PLUGIN_ID_INVALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false
    		},
    		{
    			// 2. Invalid user token
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_INVALID,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false
    		},
    		{
    			// 3. Null user token
    			ValueConstants.PLUGIN_ID_VALID,
    			null,
    			ValueConstants.STATIC_ID_VALID,
    			true,
    			false
    		},
    		{
    			// 4. Invalid Static Id
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_INVALID,
    			true,
    			false
    		},
    		{
    			// 5. Valid case
    			ValueConstants.PLUGIN_ID_VALID,
    			ValueConstants.USERTOKEN_VALID,
    			ValueConstants.STATIC_ID_VALID,
    			false,
    			false
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private String userToken;
	private int pluginId;
	private boolean expectedResult;
	
	public PluginDomain_installPluginForUser_UnitTests(int pluginId, String userToken, Long staticUserId, boolean addPluginRetval, boolean expectedResult)
	{
		this.pluginId = pluginId;
		this.userToken = userToken;
		this.expectedResult = expectedResult;
		
		IPluginAccessLayer dal = mock(IPluginAccessLayer.class);
		Mockito.when(dal.addPluginToUser(anyInt(), eq(pluginId))).thenReturn(addPluginRetval);
		
		IUserAccountDomain uas = mock(IUserAccountDomain.class);
		Mockito.when(uas.getStaticIdFromToken(userToken)).thenReturn(staticUserId);
		
		this.domain.setDataAccessLayer(dal);
		this.domain.setUserAccountService(uas);
	}
	
	@Test
	public void installPlugin()
	{
		boolean result = this.domain.installPluginForUser(pluginId, userToken);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
