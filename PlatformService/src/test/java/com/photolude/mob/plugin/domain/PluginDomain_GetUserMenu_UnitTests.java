package com.photolude.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.commons.plugins.servicemodel.MainMenuItem;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.plugin.domain.PluginDomain;
import com.photolude.mob.user.domain.IUserAccountDomain;

@RunWith(Parameterized.class)
public class PluginDomain_GetUserMenu_UnitTests {
	private static final String TOKEN_VALID = "token";
	private static final Long STATIC_ID_VALID = 1L;
	private static final MainMenuItem[] MENU_ITEMS_VALID = new MainMenuItem[]{new MainMenuItem()};
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TOKEN_VALID,
    			STATIC_ID_VALID,
    			MENU_ITEMS_VALID,
    			false,
    			MENU_ITEMS_VALID
    		},
    		{
    			null,
    			STATIC_ID_VALID,
    			MENU_ITEMS_VALID,
    			false,
    			null
    		},
    		{
    			"",
    			STATIC_ID_VALID,
    			MENU_ITEMS_VALID,
    			false,
    			null
    		},
    		{
    			TOKEN_VALID,
    			null,
    			MENU_ITEMS_VALID,
    			false,
    			null
    		},
    		{
    			TOKEN_VALID,
    			STATIC_ID_VALID,
    			null,
    			true,
    			null
    		},
    		{
    			TOKEN_VALID,
    			STATIC_ID_VALID,
    			new MainMenuItem[]{},
    			true,
    			new MainMenuItem[]{}
    		},
		});
	}
	
	private PluginDomain domain;
	private String token;
	private MainMenuItem[] expectedResult;
	private IUserAccountDomain userAccountDomain;
	private boolean userPluginsInitialized;
	
	public PluginDomain_GetUserMenu_UnitTests(String token, Long staticId, MainMenuItem[] dalRetval, boolean userPluginsInitialized, MainMenuItem[] expectedResult)
	{
		this.token = token;
		this.expectedResult = expectedResult;
		this.userPluginsInitialized = userPluginsInitialized;
		
		this.userAccountDomain = mock(IUserAccountDomain.class);
		Mockito.when(this.userAccountDomain.getStaticIdFromToken(token)).thenReturn(staticId);
		
		IPluginAccessLayer pluginDAL = mock(IPluginAccessLayer.class);
		Mockito.when(pluginDAL.getUserMenuItems(staticId)).thenReturn(dalRetval);
		
		this.domain = new PluginDomain()
						.setUserAccountService(this.userAccountDomain)
						.setDataAccessLayer(pluginDAL);
	}
	
	@Test
	public void PluginDomain_GetUserMenu_Test() throws Exception
	{
		MainMenuItem[] result = this.domain.getUserMenu(this.token);
		
		Assert.assertArrayEquals(this.expectedResult, result);
		verify(this.userAccountDomain, times((userPluginsInitialized? 1 : 0))).getStaticIdFromEmail(any(String.class));
	}
}
