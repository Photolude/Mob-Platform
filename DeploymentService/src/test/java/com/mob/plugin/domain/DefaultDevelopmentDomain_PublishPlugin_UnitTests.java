package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginDataCall;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.plugins.servicemodel.ServiceAlias;
import com.mob.commons.plugins.utils.PplUtils;
import com.mob.commons.service.clients.IUserServiceClient;
import com.mob.plugin.dal.IPluginDeploymentAccessLayer;
import com.mob.plugin.domain.DevelopmentDomain;

@RunWith(Parameterized.class)
public class DefaultDevelopmentDomain_PublishPlugin_UnitTests {
	private static final String COMPANY_NAME_VALID = "Company";
	private static final Integer PLUGIN_ID_VALID = 1;
	private static final Integer OLD_PLUGIN_ID_VALID = 12;
	private static final MainMenuItem[] MENU_VALID = new MainMenuItem[]{new MainMenuItem(1, null, null, null, 0), new MainMenuItem(4, null, null, null, 0)};
	private static final PluginScript[] SCRIPTS_VALID = new PluginScript[]{new PluginScript(3, null, null, null, null), new PluginScript(20, null, null, null, null)};
	private static final PluginDataCall[] DATACALL_VALID = new PluginDataCall[]{new PluginDataCall().setId(5), new PluginDataCall().setId(6)};
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Create Plugin
    			COMPANY_NAME_VALID,
    			null,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			false,
    			true
    		},
    		{
    			// 1. Update Plugin
    			COMPANY_NAME_VALID,
    			OLD_PLUGIN_ID_VALID,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			false,
    			true
    		},
    		{
    			// 2. Update Plugin with no previous menu items
    			COMPANY_NAME_VALID,
    			OLD_PLUGIN_ID_VALID,
    			PLUGIN_ID_VALID,
    			null,
    			22,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			false,
    			true
    		},
    		{
    			// 3. Update Plugin with no previous scripts
    			COMPANY_NAME_VALID,
    			OLD_PLUGIN_ID_VALID,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			null,
    			23,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			false,
    			true
    		},
    		{
    			// 4. Update plugin fails with menu items
    			COMPANY_NAME_VALID,
    			OLD_PLUGIN_ID_VALID,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			null,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			true,
    			false
    		},
    		{
    			// 5. Update Plugin fails with scripts
    			COMPANY_NAME_VALID,
    			OLD_PLUGIN_ID_VALID,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			null,
    			DATACALL_VALID,
    			24,
    			25,
    			false,
    			true,
    			false
    		},
    		{
    			// 6. Update plugin without data calls
    			COMPANY_NAME_VALID,
    			null,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			23,
    			null,
    			24,
    			25,
    			false,
    			false,
    			true
    		},
    		{
    			// 7. Update plugin without data calls
    			COMPANY_NAME_VALID,
    			null,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			null,
    			25,
    			false,
    			false,
    			false
    		},
    		{
    			// 8. Plug-in art retval null
    			COMPANY_NAME_VALID,
    			null,
    			PLUGIN_ID_VALID,
    			MENU_VALID,
    			22,
    			SCRIPTS_VALID,
    			23,
    			DATACALL_VALID,
    			24,
    			null,
    			false,
    			true,
    			false
    		},
		});
	}
	
	private DevelopmentDomain domain = new DevelopmentDomain();
	private IPluginDeploymentAccessLayer pluginAccessLayer;
	private Ppl ppl;
	private boolean expectedResult;
	private MainMenuItem[] previousMenu;
	private PluginScript[] previousScript;
	private PluginDataCall[] previousDataCalls;
	private boolean expectPluginDeletion;
	private boolean expectCleanUp;
	private Integer oldPluginId;
	
	public DefaultDevelopmentDomain_PublishPlugin_UnitTests(String companyName, Integer oldPluginId, Integer newPluginId, MainMenuItem[] previousMenuItems, Integer addMenuItemRetval, PluginScript[] previousScripts, Integer addScriptRetval, PluginDataCall[] previousDataCalls, Integer addDataCallRetval, Integer addArtRetval, boolean expectPluginDeletion, boolean expectCleanUp, boolean expectedResult)
	{
		this.expectedResult = expectedResult;
		this.previousMenu = previousMenuItems;
		this.previousScript = previousScripts;
		this.previousDataCalls = previousDataCalls;
		this.expectPluginDeletion = expectPluginDeletion;
		this.oldPluginId = oldPluginId;
		this.expectCleanUp = expectCleanUp;
		
		this.ppl = PplUtils.unmarshalPplFile(this.getClass().getResource(DefaultDevelopmentDomain_DeployPluginForDebugging_UnitTests.PPL_SOURCE_VALID));
		
		this.pluginAccessLayer = mock(IPluginDeploymentAccessLayer.class);
		Mockito.when(this.pluginAccessLayer.getCompanyName(any(String.class))).thenReturn(companyName);
		Mockito.when(this.pluginAccessLayer.getPluginByCompanyNameVersionToken(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(oldPluginId);
		Mockito.when(this.pluginAccessLayer.getPluginMenuItems(anyInt())).thenReturn(previousMenuItems);
		Mockito.when(this.pluginAccessLayer.getPluginScripts(anyInt())).thenReturn(previousScripts);
		Mockito.when(this.pluginAccessLayer.getPluginDataCalls(anyInt())).thenReturn(this.previousDataCalls);

		Mockito.when(this.pluginAccessLayer.addPlugin(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(ServiceAlias[].class), any(String.class), any(String.class), anyInt(), any(ExternalAttribution[].class), anyBoolean())).thenReturn(newPluginId);
		Mockito.when(this.pluginAccessLayer.addMenuItem(anyInt(), any(String.class), any(String.class), any(String.class), anyInt())).thenReturn(addMenuItemRetval);
		Mockito.when(this.pluginAccessLayer.addScript(anyInt(), anyInt(), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(addScriptRetval);
		Mockito.when(this.pluginAccessLayer.addDataCall(anyInt(), anyString(), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(addDataCallRetval);
		Mockito.when(this.pluginAccessLayer.addArt(anyInt(), anyString(), any(String.class), any(String.class))).thenReturn(addArtRetval);
		Mockito.when(this.pluginAccessLayer.updatePluginData(anyInt(), anyString(), anyString(), any(ServiceAlias[].class), anyString(), anyString(), anyString(), anyInt(), any(ExternalAttribution[].class), anyBoolean())).thenReturn(newPluginId != null);

		IUserServiceClient accountDomain = mock(IUserServiceClient.class);
		
		this.domain.setDataAccessLayer(this.pluginAccessLayer)
					.setUserAccountService(accountDomain);
	}
	
	@Test
	public void publishPlugin() throws Exception
	{
		boolean actual = this.domain.publishPlugin(this.ppl);
		
		Assert.assertEquals(this.expectedResult, actual);
		
		if(this.expectPluginDeletion)
		{
			if(this.oldPluginId != null)
			{
				Mockito.verify(this.pluginAccessLayer).deletePlugin(this.oldPluginId);
			}
		}
	}
}
