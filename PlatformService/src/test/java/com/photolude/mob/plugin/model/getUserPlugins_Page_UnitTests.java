package com.photolude.mob.plugin.model;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.services.PluginManagementSvc;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;

@RunWith(Parameterized.class)
public class getUserPlugins_Page_UnitTests extends ServiceLogicBase {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
			//
			// Page name tests
			//
			{
    			//1. null page name
				VALID_USER_ID,
    			null,
    			PAGE_VALID,
    			PAGE_VALID, 
    			PluginManagementSvc.STATUS_SUCCEEDED
			}, 
			{
    			//2. Empty page name
				VALID_USER_ID,
    			"",
    			PAGE_VALID,
    			PAGE_VALID, 
				PluginManagementSvc.STATUS_SUCCEEDED
			}, 
    		{
    			//3. Page name w/ just a space
				VALID_USER_ID,
    			" ",
    			PAGE_VALID,
    			PAGE_VALID, 
				PluginManagementSvc.STATUS_SUCCEEDED
			}, 
    		{
    			//4. Invalid page name
				VALID_USER_ID,
    			INVALID_PAGE_NAME,
    			PAGE_VALID,
    			PAGE_VALID,  
				PluginManagementSvc.STATUS_SUCCEEDED
			}, 
		});
    }
	
	public getUserPlugins_Page_UnitTests(String userName, String pageName, PluginPage rawData, PluginPage expectedResult, int validStatusCode) {
		super(userName, pageName, rawData, expectedResult, validStatusCode);
	}
}
