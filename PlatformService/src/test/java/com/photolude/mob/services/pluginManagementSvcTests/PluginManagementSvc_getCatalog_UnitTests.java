package com.photolude.mob.services.pluginManagementSvcTests;

import java.util.Arrays;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.photolude.mob.plugin.domain.IPluginDomain;
import com.photolude.mob.services.PluginManagementSvc;
import com.photolude.mob.plugins.commons.servicemodel.PluginCatalog;

@RunWith(Parameterized.class)
public class PluginManagementSvc_getCatalog_UnitTests {
	private static final String TOKEN_VALID = "This is a token";
	private static final PluginCatalog CATALOG_VALID = new PluginCatalog();
	private static final int STATUS_OK = 200;
	private static final int STATUS_FAIL = 500;
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TOKEN_VALID,
    			CATALOG_VALID,
    			STATUS_OK
    		},
    		{
    			null,
    			CATALOG_VALID,
    			STATUS_OK
    		},
    		{
    			"",
    			CATALOG_VALID,
    			STATUS_OK
    		},

    		{
    			TOKEN_VALID,
    			null,
    			STATUS_FAIL
    		},
		});
	}
	
	private PluginManagementSvc service = new PluginManagementSvc();
	private IPluginDomain domain = mock(IPluginDomain.class);
	private String token;
	private int expectedStatus;
	private PluginCatalog catalog;
	
	public PluginManagementSvc_getCatalog_UnitTests(String token, PluginCatalog catalog, int expectedStatus)
	{
		this.token = token;
		this.expectedStatus = expectedStatus;
		this.catalog = catalog;
		
		Mockito.when(this.domain.getCatalog(token)).thenReturn(catalog);
		
		this.service.setDomain(domain);
	}
	
	@Test
	public void getCatalogTest() throws Exception
	{
		Response response = this.service.getCatalog(this.token);
		
		verify(this.domain, times(1)).getCatalog(this.token);
		Assert.assertEquals(this.expectedStatus, response.getStatus());
		
		if(this.expectedStatus == STATUS_OK)
		{
			Assert.assertEquals(this.catalog, response.getEntity());
		}
	}
}
