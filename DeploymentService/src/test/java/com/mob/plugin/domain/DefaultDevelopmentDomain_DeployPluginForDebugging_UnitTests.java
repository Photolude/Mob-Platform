package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.mob.commons.plugins.servicemodel.ServiceAlias;
import com.mob.commons.plugins.utils.PplUtils;
import com.mob.plugin.dal.IPluginDeploymentAccessLayer;
import com.mob.plugin.domain.DevelopmentDomain;

import org.mockito.Mockito;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class DefaultDevelopmentDomain_DeployPluginForDebugging_UnitTests {
	private static final String TOKEN_VALID = "valid token";
	public static final String PPL_SOURCE_VALID = "/validServerPpl.xml";
	private static final Integer MENU_ID_VALID = 1;
	private static final Integer DATACALL_ID_VALID = 1;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TOKEN_VALID,
    			PPL_SOURCE_VALID,
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			true
    		},
    		{
    			TOKEN_VALID,
    			"/validServerPpl_MissingMenu.xml",
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			true
    		},
    		{
    			"",
    			PPL_SOURCE_VALID,
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			false
    		},
    		{
    			null,
    			PPL_SOURCE_VALID,
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			false
    		},
    		{
    			TOKEN_VALID,
    			null,
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			false
    		},
    		{
    			TOKEN_VALID,
    			"/invalidImageServerPpl.xml",
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			null,
    			false
    		},
    		{
    			TOKEN_VALID,
    			PPL_SOURCE_VALID,
    			MENU_ID_VALID,
    			DATACALL_ID_VALID,
    			1,
    			true
    		},
    		{
    			TOKEN_VALID,
    			PPL_SOURCE_VALID,
    			null,
    			DATACALL_ID_VALID,
    			null,
    			false
    		},
    		{
    			TOKEN_VALID,
    			PPL_SOURCE_VALID,
    			MENU_ID_VALID,
    			null,
    			1,
    			false
    		},
		});
	}
	
	private DevelopmentDomain domain = new DevelopmentDomain();
	private IPluginDeploymentAccessLayer dal;
	private String userToken;
	private Ppl pluginRequest;
	private boolean expectedResult;
	private Integer previousId;
	
	public DefaultDevelopmentDomain_DeployPluginForDebugging_UnitTests(String userToken, String pplSource, Integer menuId, Integer datacallId, Integer previousId, boolean expectedResult)
	{
		this.userToken = userToken;
		this.expectedResult = expectedResult;
		this.previousId = previousId;
		
		this.pluginRequest = null;
		if(pplSource != null)
		{
			this.pluginRequest = PplUtils.unmarshalPplFile(this.getClass().getResource(pplSource));
		}
		
		this.dal = mock(IPluginDeploymentAccessLayer.class);
		Mockito.when(this.dal.addPlugin(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(ServiceAlias[].class), anyString(), anyString(), anyInt(), any(ExternalAttribution[].class), anyBoolean())).thenReturn(1);
		Mockito.when(this.dal.addScript(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(2);
		Mockito.when(this.dal.getCompanyName(anyString())).thenReturn("Company X");
		Mockito.when(this.dal.getPluginByCompanyNameVersionToken(anyString(), anyString(), anyString(), anyString())).thenReturn(previousId);
		Mockito.when(this.dal.addMenuItem(anyInt(), anyString(), anyString(), anyString(), anyInt())).thenReturn(menuId);
		Mockito.when(this.dal.addDataCall(anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(datacallId);
		Mockito.when(this.dal.addArt(anyInt(), anyString(), anyString(), anyString())).thenReturn(1);
		this.domain.setDataAccessLayer(this.dal);
	}
	
	@Test
	public void DeployPluginForDebugging() throws Exception
	{
		boolean actual = this.domain.DeployPluginForDebugging(this.userToken, this.pluginRequest);
		Assert.assertEquals(this.expectedResult, actual);
		
		if(this.previousId != null)
		{
			verify(this.dal, atLeast(1)).deletePlugin(this.previousId);
		}
		else if(this.expectedResult)
		{
			verify(this.dal, times(0)).deletePlugin(anyInt());
		}
	}
}
