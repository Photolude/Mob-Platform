package com.mob.plugin.domain;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.plugin.dal.IPluginAccessLayer;
import com.mob.plugin.domain.PluginDomain;

@RunWith(Parameterized.class)
public class PluginDomain_getArt_UnitTests {
	private static final String TOKEN_VALID = "123";
	private static final String ROLE_VALID = "role";
	private static final String ART_PATH_VALID = "art";
	private static final PluginArt ART_VALID = new PluginArt().setId(1).setData("Data").setContentType("ContentType");
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			TOKEN_VALID,
    			ROLE_VALID,
    			ART_PATH_VALID,
    			ART_VALID,
    			ART_VALID
    		},
    		{
    			// 1. Null Token
    			null,
    			ROLE_VALID,
    			ART_PATH_VALID,
    			ART_VALID,
    			null
    		},
    		{
    			// 2. Empty Token
    			"",
    			ROLE_VALID,
    			ART_PATH_VALID,
    			ART_VALID,
    			null
    		},
    		{
    			// 3. Null Role
    			TOKEN_VALID,
    			null,
    			ART_PATH_VALID,
    			ART_VALID,
    			null
    		},
    		{
    			// 4. Empty Role
    			TOKEN_VALID,
    			"",
    			ART_PATH_VALID,
    			ART_VALID,
    			null
    		},
    		{
    			// 5. Null path
    			TOKEN_VALID,
    			ROLE_VALID,
    			null,
    			ART_VALID,
    			null
    		},
    		{
    			// 6. Empty path
    			TOKEN_VALID,
    			ROLE_VALID,
    			"",
    			ART_VALID,
    			null
    		},
    		{
    			// 7. Null art
    			TOKEN_VALID,
    			ROLE_VALID,
    			ART_PATH_VALID,
    			null,
    			null
    		},
		});
	}
	
	private PluginDomain domain = new PluginDomain();
	private String userToken;
	private String artPath;
	private String role;
	private PluginArt expectedResult;
	
	public PluginDomain_getArt_UnitTests(String userToken, String role, String artPath, PluginArt art, PluginArt expectedResult)
	{
		this.userToken = userToken;
		this.artPath = artPath;
		this.role = role;
		this.expectedResult = expectedResult;
		
		IPluginAccessLayer pluginAccessLayer = mock(IPluginAccessLayer.class);
		Mockito.when(pluginAccessLayer.getArt(userToken, role, artPath)).thenReturn(art);
		
		this.domain.setDataAccessLayer(pluginAccessLayer);
	}
	
	@Test
	public void getArt() throws Exception
	{
		PluginArt result = this.domain.getArt(this.userToken, this.role, this.artPath);
		
		Assert.assertEquals(this.expectedResult, result);
	}
}
