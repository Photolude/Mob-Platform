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

import com.photolude.mob.commons.plugins.servicemodel.PluginDefinition;
import com.photolude.mob.commons.plugins.servicemodel.PluginPage;
import com.photolude.mob.commons.plugins.servicemodel.PluginScript;
import com.photolude.mob.commons.plugins.servicemodel.ServiceAlias;
import com.photolude.mob.plugin.dal.IPluginAccessLayer;
import com.photolude.mob.plugin.domain.PluginDomain;
import com.photolude.mob.user.domain.IUserAccountDomain;

@RunWith(Parameterized.class)
public class PluginDomain_getPagePlugins_UnitTests {
	private static final String JAVASCRIPT_VALUE = "This is a test";
	private static final String JAVASCRIPT_TYPE = "text/javascript";
	
	private static final String HTML_TYPE = "html";
	private static final String HTML_VALUE = "<html></html>";
	
	private static final String PAGE_VALID = "This is a page";
	private static final String USER_VALID = "This is a token";
	private static final Long USER_STATIC_ID_VALID = 1L;
	
	public static final ServiceAlias[] SERVICE_ALIAS_VALID = new ServiceAlias[]{new ServiceAlias().setName("Test").setEndpoint("http://test/test")};
	
	public static final PluginScript[] SCRIPT_1_VALID =  new PluginScript[] { new PluginScript().setScript(JAVASCRIPT_VALUE).setType(JAVASCRIPT_TYPE).setPage("Page") };
	public static final PluginScript[] SCRIPT_2_VALID =  new PluginScript[] { new PluginScript().setScript(JAVASCRIPT_VALUE).setType(JAVASCRIPT_TYPE).setPage("Page"), new PluginScript().setScript(HTML_VALUE).setType(HTML_TYPE).setPage("Page") };
	public static final PluginScript[] SCRIPT_RESULT_FAILED = null;
	
	public static final PluginDefinition[] PLUGINS_VALID = new PluginDefinition[]{new PluginDefinition().setCompany("Company X").setId(1).setName("Plugin").setServiceAliases(SERVICE_ALIAS_VALID).setRole("Role") };
	public static final PluginDefinition[] PLUGINS_RESULT_FAILED = new PluginDefinition[0];
	
	public static final PluginPage PAGE_RESULT_VALID = new PluginPage().setScripts(SCRIPT_1_VALID).setPlugins(PLUGINS_VALID);
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{

    		{
    			// 0. Valid javascript
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			PAGE_RESULT_VALID
    		},
    		{
    			// 1. Multiple javascripts
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			SCRIPT_2_VALID,
    			PLUGINS_VALID,
    			new PluginPage().setScripts(SCRIPT_2_VALID).setPlugins(PLUGINS_VALID)
    		},
    		{
    			// 2. Null page
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			null,
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		{
    			// 3. Empty string page
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			"",
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		
    		{
    			// 4. User Null
    			null,
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		{
    			// 5. User empty string
    			"",
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		
    		{
    			// 6. Valid javascript
    			USER_VALID,
    			null,
    			PAGE_VALID,
    			SCRIPT_1_VALID,
    			PLUGINS_VALID,
    			null
    		},
    		{
    			// 7. Valid javascript
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			null,
    			PLUGINS_VALID,
    			new PluginPage().setScripts(new PluginScript[0]).setPlugins(new PluginDefinition[0])
    		},
    		{
    			// 8. All generic scripts
    			USER_VALID,
    			USER_STATIC_ID_VALID,
    			PAGE_VALID,
    			new PluginScript[]{new PluginScript().setPage("*")},
    			PLUGINS_VALID,
    			new PluginPage().setScripts(new PluginScript[0]).setPlugins(new PluginDefinition[0])
    		},
		});
	}
	
	private PluginDomain domain;
	private String page;
	private PluginPage expectedResults;
	private String userToken;
	
	public PluginDomain_getPagePlugins_UnitTests(String userToken, Long userStaticId, String page, PluginScript[] serviceData, PluginDefinition[] pluginData, PluginPage expectedResults)
	{
		this.page = page;
		this.expectedResults = expectedResults;
		this.userToken = userToken;
		
		IUserAccountDomain userService = mock(IUserAccountDomain.class);
		Mockito.when(userService.getStaticIdFromToken(userToken)).thenReturn(userStaticId);
		
		IPluginAccessLayer mockDAL = mock(IPluginAccessLayer.class);
		Mockito.when(mockDAL.getPageScripts(userStaticId, page)).thenReturn(serviceData);
		Mockito.when(mockDAL.getPagePlugins(userStaticId, page)).thenReturn(pluginData);
		
		this.domain = new PluginDomain()
						.setDataAccessLayer(mockDAL)
						.setUserAccountService(userService);
	}
	
	@Test
	public void getPagePluginsTest() throws Exception
	{
		PluginPage actual = this.domain.getPagePlugins(this.userToken, this.page);
		
		Assert.assertEquals(this.expectedResults, actual);
	}
}
