package com.mob.www.platform.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;

import com.mob.commons.plugins.servicemodel.PluginArt;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.service.clients.IPluginService;
import com.mob.www.platform.constants.TestConstants;
import com.mob.www.platform.controller.ArtController;
import com.mob.www.platform.services.IArtCache;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class ArtController_getArt_UnitTests {
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid no cached response
    			TestConstants.ROLE_VALID,
    			TestConstants.ART_PATH_VALID,
    			null,
    			null,
    			TestConstants.ART_VALID,
    			TestConstants.BODY_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.STATUS_CODE_OK
    		},
    		{
    			// 1. No Art returned
    			TestConstants.ROLE_VALID,
    			TestConstants.ART_PATH_VALID,
    			null,
    			null,
    			null,
    			null,
    			null,
    			TestConstants.STATUS_CODE_FAIL
    		},
    		{
    			// 2. Invalid role (null)
    			null,
    			TestConstants.ART_PATH_VALID,
    			null,
    			null,
    			TestConstants.ART_VALID,
    			null,
    			null,
    			TestConstants.STATUS_CODE_FAIL
    		},
    		{
    			// 3. Invalid role (empty)
    			"",
    			TestConstants.ART_PATH_VALID,
    			TestConstants.ART_VALID,
    			null,
    			null,
    			null,
    			null,
    			TestConstants.STATUS_CODE_FAIL
    		},
    		{
    			// 4. Invalid path (null)
    			TestConstants.ROLE_VALID,
    			null,
    			null,
    			null,
    			TestConstants.ART_VALID,
    			null,
    			null,
    			TestConstants.STATUS_CODE_FAIL
    		},
    		{
    			// 4. Invalid path (empty)
    			TestConstants.ROLE_VALID,
    			"",
    			null,
    			null,
    			TestConstants.ART_VALID,
    			null,
    			null,
    			TestConstants.STATUS_CODE_FAIL
    		},
    		{
    			// 5. Cached result available
    			TestConstants.ROLE_VALID,
    			TestConstants.ART_PATH_VALID,
    			TestConstants.ART_VALID,
    			TestConstants.PLUGIN_VALID,
    			null,
    			TestConstants.BODY_VALID,
    			TestConstants.CONTENT_TYPE_VALID,
    			TestConstants.STATUS_CODE_OK
    		},
		});
	}
	
	private ArtController controller = new ArtController();
	private String role;
	private String artPath;
	private int responseCode;
	private String expectedContentType;
	
	public ArtController_getArt_UnitTests(String role, String artPath, PluginArt cachedArtResponse, PluginDefinition definitionForRole, PluginArt artResponse, String expectedBody, String expectedContentType, int responseCode)
	{
		this.role = role;
		this.artPath = artPath;
		this.expectedContentType = expectedContentType;
		this.responseCode = responseCode;
		
		IPluginService pluginService = mock(IPluginService.class);
		Mockito.when(pluginService.getArt(any(String.class), eq(role), eq(artPath))).thenReturn(artResponse);
		Mockito.when(pluginService.getPluginForUserRole(anyString(), anyString())).thenReturn(definitionForRole);
		
		IArtCache cacheService = mock(IArtCache.class);
		Mockito.when(cacheService.getArt(anyInt(), anyString())).thenReturn(cachedArtResponse);
		
		this.controller.setPluginService(pluginService)
						.setCache(cacheService);
	}
	
	@Test
	public void getArt() throws Exception
	{
		ServletOutputStream stream = mock(ServletOutputStream.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Mockito.when(response.getOutputStream()).thenReturn(stream);
		
		HttpSession session = mock(HttpSession.class);
		Mockito.when(session.getAttribute(PlatformController.SESSION_USER_TOKEN)).thenReturn("token");
		Mockito.when(session.getAttribute(ArtController.SESSION_ROLE_MAP)).thenReturn(null);
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.when(request.getSession(false)).thenReturn(session);
		
		this.controller.getArt(role, artPath, request, response);
		verify(response, times(1)).setStatus(this.responseCode);
		if(this.expectedContentType != null)
		{
			verify(response, times(1)).setContentType(this.expectedContentType);
		}
	}
}
