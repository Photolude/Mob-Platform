package com.photolude.mob.plugin.model;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.services.PluginManagementSvc;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;

@RunWith(Parameterized.class)
public class getUserPlugins_DALMerge_UnitTests extends ServiceLogicBase {

	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{ 
				//13. Return single valid result
				VALID_USER_ID,
				VALID_PAGE_NAME,
				PAGE_VALID,
				PAGE_VALID, 
				PluginManagementSvc.STATUS_SUCCEEDED
			},
    		{
				//14. return single valid with multiple scripts
				VALID_USER_ID,
				VALID_PAGE_NAME,
				PAGE_VALID,
				PAGE_VALID, 
				PluginManagementSvc.STATUS_SUCCEEDED
			},
			{
				//16. Multiple results
				VALID_USER_ID,
				VALID_PAGE_NAME,
				new PluginPage()
					.setScripts(
				new PluginScript[]
				{
					new PluginScript().setType("HTML").setScript("<div></div>"),
					new PluginScript().setType("HTML").setScript("<div>2</div>")
				}).setPlugins(PLUGINS_VALID),
				new PluginPage()
				.setScripts(
				new PluginScript[]
				{
					new PluginScript().setType("HTML").setScript("<div></div>"),
					new PluginScript().setType("HTML").setScript("<div>2</div>")
				}).setPlugins(PLUGINS_VALID),
    			PluginManagementSvc.STATUS_SUCCEEDED 
			},
		});
    }
	
	public getUserPlugins_DALMerge_UnitTests(String userName, String pageName, PluginPage rawData, PluginPage expectedResult, int validStatusCode) {
		super(userName, pageName, rawData, expectedResult, validStatusCode);
	}

}
