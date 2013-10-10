package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.commons.plugins.servicemodel.PluginDefinition;

@RunWith(Parameterized.class)
public class PluginDomain_getUserPlugins_UserId_UnitTests extends PluginDomain_getUserPagePlugins_LogicBase {

	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
        	//
    		// User Id Tests
    		//
    		{
    			//0. null user id
    			null,
    			new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				null
			}, 
			{
    			//1. Empty user id
    			"",
    			new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				null
			}, 
    		{
    			//2. User id just a space
    			" ",
    			new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				null
			}, 
    		{
    			//3. Invalid user id
    			INVALID_USER_ID,
    			new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				null
			}, 
		});
    }
	
	public PluginDomain_getUserPlugins_UserId_UnitTests(String userName, PluginDefinition[] rawData, PluginDefinition[] expectedResult) {
		super(userName, rawData, expectedResult);
	}
}
