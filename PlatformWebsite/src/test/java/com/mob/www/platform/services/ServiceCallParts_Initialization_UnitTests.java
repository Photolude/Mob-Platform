package com.mob.www.platform.services;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ServiceCallParts_Initialization_UnitTests {
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid with params
    			"role/plugin/param/extras",
    			"role/plugin",
    			"/param/extras"
    		},
    		{
    			// 1. Valid without params
    			"role/plugin",
    			"role/plugin",
    			null
    		},
    		{
    			// 2. Valid with empty params
    			"role/plugin/",
    			"role/plugin",
    			"/"
    		},
    		{
    			// 3. Incomplete Alias
    			"role",
    			"role",
    			null
    		},
    		{
    			// 4. Null service call
    			null,
    			null,
    			null
    		},
    		{
    			// 5. empty service call
    			"",
    			"",
    			null
    		}
		});
	}
	
	private String serviceCall;
	private String expectedAlias;
	private String expectedParams;
	
	public ServiceCallParts_Initialization_UnitTests(String serviceCall, String expectedAlias, String expectedParams)
	{
		this.serviceCall = serviceCall;
		this.expectedAlias = expectedAlias;
		this.expectedParams = expectedParams;
	}
	
	@Test
	public void InitializationTest() throws Exception
	{
		ServiceCallParts parts = new ServiceCallParts(this.serviceCall);
		
		Assert.assertEquals(this.expectedAlias, parts.getAlias());
		Assert.assertEquals(this.expectedParams, parts.getPathParams());
	}
}
