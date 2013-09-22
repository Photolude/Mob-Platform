package com.photolude.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.commons.plugins.ppl.Ppl;
import com.photolude.mob.commons.plugins.servicemodel.ExternalAttribution;
import com.photolude.mob.commons.plugins.servicemodel.ServiceAlias;
import com.photolude.mob.commons.plugins.utils.PplUtils;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.plugin.domain.DefaultDevelopmentDomain;

import org.mockito.Mockito;
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
	
	private DefaultDevelopmentDomain domain = new DefaultDevelopmentDomain();
	private IPluginAccessLayer dal;
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
		
		this.dal = mock(IPluginAccessLayer.class);
		Mockito.when(this.dal.addPlugin(any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(ServiceAlias[].class), any(String.class), any(String.class), anyInt(), any(ExternalAttribution[].class))).thenReturn(1);
		Mockito.when(this.dal.addScript(anyInt(), anyInt(), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(2);
		Mockito.when(this.dal.getCompanyName(any(String.class))).thenReturn("Company X");
		Mockito.when(this.dal.getPluginByCompanyNameVersionToken(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(previousId);
		Mockito.when(this.dal.addMenuItem(anyInt(), any(String.class), any(String.class), any(String.class), anyInt())).thenReturn(menuId);
		Mockito.when(this.dal.addDataCall(anyInt(), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(datacallId);
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
