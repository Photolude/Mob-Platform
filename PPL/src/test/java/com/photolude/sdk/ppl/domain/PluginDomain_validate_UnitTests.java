package com.photolude.sdk.ppl.domain;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.commons.plugins.ppl.PluginType;
import com.photolude.mob.commons.plugins.ppl.Ppl;
import com.photolude.mob.commons.plugins.utils.PplUtils;

@RunWith(Parameterized.class)
public class PluginDomain_validate_UnitTests {
	private PhotoludePluginDomain domain = new PhotoludePluginDomain();
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{"/validPpl.xml", true},
		});
	}
	
	private String resource;
	private boolean expectedResult;
	
	public PluginDomain_validate_UnitTests(String resource, boolean expectedResult)
	{
		this.resource = resource;
		this.expectedResult = expectedResult;
	}
	
	@Test
	public void TestPplFile() throws Exception
	{
		Logger.getLogger(this.getClass()).info("Starting test with " + this.resource);
		
		URL url = this.getClass().getResource(this.resource);
		Ppl ppl = PplUtils.unmarshalPplFile(url);
		
		Assert.assertEquals(this.expectedResult, this.domain.validate(ppl));
		PluginType plugin = ppl.getPlugin().get(0);
		Assert.assertNotNull(plugin.getTags());
	}
}
