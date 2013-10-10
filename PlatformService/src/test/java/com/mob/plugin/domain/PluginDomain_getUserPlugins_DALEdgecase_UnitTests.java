package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.commons.plugins.servicemodel.PluginDefinition;

@RunWith(Parameterized.class)
public class PluginDomain_getUserPlugins_DALEdgecase_UnitTests extends PluginDomain_getUserPagePlugins_LogicBase {

	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
			//
			// Return data tests
			//
    		{
    			//0. No data returned
    			VALID_USER_ID,
    			null,
    			new PluginDefinition[0]
			},
    		{
				//1. Empty data returned
				VALID_USER_ID,
				new PluginDefinition[0],
				new PluginDefinition[0]
			},
			{ 
				//2. Return single valid result
				VALID_USER_ID,
				new PluginDefinition[] { null },
				new PluginDefinition[] { null }
			},
		});
    }
	
	public PluginDomain_getUserPlugins_DALEdgecase_UnitTests(String userName, PluginDefinition[] rawData, PluginDefinition[] expectedResult) {
		super(userName, rawData, expectedResult);
	}

}
