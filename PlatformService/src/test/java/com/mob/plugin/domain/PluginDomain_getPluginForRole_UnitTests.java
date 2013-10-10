package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.plugin.constants.Constants;
import com.mob.plugin.constants.PluginDefinitionConstants;
import com.mob.plugin.dal.IPluginAccessLayer;
import com.mob.plugin.domain.PluginDomain;

@RunWith(Parameterized.class)
public class PluginDomain_getPluginForRole_UnitTests {
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			Constants.TOKEN_VALID,
    			Constants.ROLE_VALID,
    			PluginDefinitionConstants.DEFINITION_VALID,
    			PluginDefinitionConstants.DEFINITION_VALID
    		},
    		{
    			// 1. Token null
    			null,
    			Constants.ROLE_VALID,
    			PluginDefinitionConstants.DEFINITION_VALID,
    			null
    		},
    		{
    			// 2. Token empty
    			"",
    			Constants.ROLE_VALID,
    			PluginDefinitionConstants.DEFINITION_VALID,
    			null
    		},
    		{
    			// 3. Role null
    			Constants.TOKEN_VALID,
    			null,
    			PluginDefinitionConstants.DEFINITION_VALID,
    			null
    		},
    		{
    			// 4. Role empty
    			Constants.TOKEN_VALID,
    			"",
    			PluginDefinitionConstants.DEFINITION_VALID,
    			null
    		},
    		{
    			// 5. Valid
    			Constants.TOKEN_VALID,
    			Constants.ROLE_VALID,
    			null,
    			null
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private String userToken;
	private String role;
	private PluginDefinition expectedResult;
	
	public PluginDomain_getPluginForRole_UnitTests(String userToken, String role, PluginDefinition resultFromDal, PluginDefinition expectedResult)
	{
		this.userToken = userToken;
		this.role = role;
		this.expectedResult = expectedResult;
		
		IPluginAccessLayer dal = mock(IPluginAccessLayer.class);
		Mockito.when(dal.getPluginByUserAndRole(userToken, role)).thenReturn(resultFromDal);
		
		this.domain.setDataAccessLayer(dal);
	}
	
	@Test
	public void getPluginForRole() throws Exception
	{
		PluginDefinition result = this.domain.getPluginForRole(userToken, role);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
