package com.photolude.mob.plugins.commons;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mob.commons.plugins.ppl.Ppl;
import com.mob.commons.plugins.utils.PplUtils;

@RunWith(Parameterized.class)
public class PplUtils_validate_UnitTests {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{"/validPpl.xml", false, true, true},
    		{"/MismatchPpl.xml", false, false, false},
    		{"/InvalidHtmlPathPpl.xml", false, false, false},
    		{"/InvalidScriptPathPpl.xml", false, false, false},
    		{"/InvalidScriptPathPpl.xml", false, false, false},
    		{"/missingIconPpl.xml", false, false, false},
    		
    		{"/validServerPpl.xml", true, false, true},
    		{"/validPpl.xml", true, false, false},
		});
	}
	
	private String resource;
	private boolean serverCriteria;
	private boolean expectedResult;
	private boolean expectTags;
	
	public PplUtils_validate_UnitTests(String resource, boolean serverCriteria, boolean expectTags, boolean expectedResult)
	{
		this.resource = resource;
		this.serverCriteria = serverCriteria;
		this.expectedResult = expectedResult;
		this.expectTags = expectTags;
	}
	
	@Test
	public void TestPplFile() throws Exception
	{
		Logger.getLogger(this.getClass()).info("Starting test with " + this.resource);
		
		URL url = this.getClass().getResource(this.resource);
		Assert.assertNotNull(url);
		Ppl ppl = PplUtils.unmarshalPplFile(url);
		Assert.assertNotNull(ppl);
		
		Assert.assertEquals(this.expectedResult, PplUtils.validate(ppl, this.serverCriteria));
		
		if(this.expectTags)
		{
			Assert.assertNotNull(ppl.getPlugin().get(0).getTags());
		}
	}
}
