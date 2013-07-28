package com.photolude.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.plugin.domain.PluginDomain;
import com.photolude.mob.user.domain.IUserAccountDomain;
import com.photolude.mob.plugins.commons.servicemodel.PluginDefinition;
import com.photolude.mob.plugins.commons.servicemodel.WebPage;

@RunWith(Parameterized.class)
public class PluginDomain_getPages_UnitTests {
	private static final String DEFAULT_PLUGIN_USER = "defaultuser";
	private static final String USER_VALID = "This is a user";
	private static final WebPage[] WEBPAGE_RESULTS_0 = new WebPage[]{ };
	private static final WebPage[] WEBPAGE_RESULTS_1 = new WebPage[]{ new WebPage() };
	private static final WebPage[] WEBPAGE_RESULTS_2 = new WebPage[]{ new WebPage(), new WebPage() };
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid single result
    			USER_VALID,
    			WEBPAGE_RESULTS_1,
    			null,
    			WEBPAGE_RESULTS_1,
    			0
    		},
    		{
    			// 1. Valid multiple results
    			USER_VALID,
    			WEBPAGE_RESULTS_2,
    			null,
    			WEBPAGE_RESULTS_2,
    			0
    		},
    		{
    			// 2. Empty user name
    			"",
    			WEBPAGE_RESULTS_1,
    			WEBPAGE_RESULTS_2,
    			null,
    			0
    		},
    		{
    			// 3. Null user name
    			null,
    			WEBPAGE_RESULTS_1,
    			WEBPAGE_RESULTS_2,
    			null,
    			0
    		},
    		{
    			// 4. No results
    			USER_VALID,
    			WEBPAGE_RESULTS_0,
    			WEBPAGE_RESULTS_1,
    			WEBPAGE_RESULTS_1,
    			0
    		},
    		{
    			// 5. Null results
    			USER_VALID,
    			null,
    			WEBPAGE_RESULTS_1,
    			WEBPAGE_RESULTS_1,
    			1
    		},
    		{
    			// 5. Null results
    			USER_VALID,
    			null,
    			WEBPAGE_RESULTS_2,
    			WEBPAGE_RESULTS_2,
    			2
    		},
		});
	}
	
	private PluginDomain domain;
	private String usertoken;
	private WebPage[] expectedResults;
	private IPluginAccessLayer mockDAL;
	
	public PluginDomain_getPages_UnitTests(String usertoken, WebPage[] dalResults, WebPage[] defaultResults, WebPage[] expectedResults, int expectedAddPageCalls)
	{
		this.usertoken = usertoken;
		this.expectedResults = expectedResults;
		
		IUserAccountDomain mockUserClient = mock(IUserAccountDomain.class);
		Mockito.when(mockUserClient.getStaticIdFromToken(usertoken)).thenReturn(1L);
		Mockito.when(mockUserClient.getStaticIdFromEmail(DEFAULT_PLUGIN_USER)).thenReturn(2L);
		
		this.mockDAL = mock(IPluginAccessLayer.class);
		Mockito.when(this.mockDAL.getPages(1L)).thenReturn(dalResults).thenReturn(defaultResults);
		Mockito.when(this.mockDAL.getUserPlugins(2L)).thenReturn(new PluginDefinition[]{});
		//Mockito.when(this.mockDAL.addPageToUser(0, 1L)).thenReturn(true);
		
		this.domain = new PluginDomain()
						.setDataAccessLayer(mockDAL)
						.setUserAccountService(mockUserClient)
						.setDefaultPluginUser(DEFAULT_PLUGIN_USER);
	}
	
	@Test
	public void TestGetPages()
	{
		WebPage[] actualResults = this.domain.getPages(this.usertoken);
		
		if(this.expectedResults != null)
		{
			Assert.assertArrayEquals(this.expectedResults, actualResults);
		}
		else
		{
			Assert.assertNull(actualResults);
		}
		
		//verify(this.mockDAL, atLeast(this.expectedAddPageCalls)).addPageToUser(0, 1L);
	}
}
