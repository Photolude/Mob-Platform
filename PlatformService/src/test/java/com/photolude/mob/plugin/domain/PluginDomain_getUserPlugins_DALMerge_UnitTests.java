package com.photolude.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;

@RunWith(Parameterized.class)
public class PluginDomain_getUserPlugins_DALMerge_UnitTests extends PluginDomain_getUserPagePlugins_LogicBase {

	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{ 
				//13. Return single valid result
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
											.setName(PLUGIN_NAME)
											.setCompany(COMPANY_DEFAULT)
											.setVersion(VERSION_1_0)
				}
			},
    		{
				//14. return single valid with multiple scripts
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0),
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME) 
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				}
			},
    		{
				//15. Multiple results
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName("Plugin1")
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0),
					new PluginDefinition().setId(1)
										.setName("Plugin2")
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
										.setName("Plugin1")
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0), 
					new PluginDefinition().setId(1)
										.setName("Plugin2")
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0)
    			}
			},
			{
				//16. Multiple results
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0),
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_2_0)
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0), 
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_2_0)
    			} 
			},
			{
				//17. Multiple results
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0),
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_2)
										.setVersion(VERSION_1_0)
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0), 
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_2)
										.setVersion(VERSION_1_0)
    			}
			},

			{
				//18. No page specific plugin
				VALID_USER_ID,
				new PluginDefinition[]
				{
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0),
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_2)
										.setVersion(VERSION_1_0)
				},
				new PluginDefinition[]
				{ 
					new PluginDefinition().setId(0)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_DEFAULT)
										.setVersion(VERSION_1_0), 
					new PluginDefinition().setId(1)
										.setName(PLUGIN_NAME)
										.setCompany(COMPANY_2)
										.setVersion(VERSION_1_0)
    			}
			},
		});
    }
	
	public PluginDomain_getUserPlugins_DALMerge_UnitTests(String userName, PluginDefinition[] rawData, PluginDefinition[] expectedResult) {
		super(userName, rawData, expectedResult);
	}

}
