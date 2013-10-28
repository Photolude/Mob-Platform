package com.mob.www.platform.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import com.mob.commons.plugins.servicemodel.PluginPage;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mob.commons.service.clients.IPluginService;
import com.mob.www.platform.controller.AppsController;
import com.mob.www.platform.controller.PlatformController;
import com.mob.www.platform.services.IServiceCallManager;
import com.mob.www.platform.services.IServiceContracts;
import com.mob.www.platform.services.ServiceCallContext;

import static org.mockito.Mockito.*;


@RunWith(Parameterized.class)
public class PlatformController_pluginAppGet_UnitTests {
	private static final String TARGET_VALID = "ValidTarget";
	private static final String RESULT_SUCCEEDED = "index";
	private static final String RESULT_FAIL = "redirect:/";
	private static final PluginScript[] PLUGIN_SCRIPTS_VALID = new PluginScript[]{new PluginScript()};
	private static final PluginPage PLUGIN_PAGE_VALID = new PluginPage().setScripts(PLUGIN_SCRIPTS_VALID);
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TARGET_VALID,
    			RESULT_SUCCEEDED,
    			true,
    			PLUGIN_PAGE_VALID,
    			PLUGIN_PAGE_VALID
    		},
    		{
    			"",
    			RESULT_FAIL,
    			false,
    			PLUGIN_PAGE_VALID,
    			null,
    		},
    		{
    			null,
    			RESULT_FAIL,
    			false,
    			PLUGIN_PAGE_VALID,
    			null
    		},
    		{
    			TARGET_VALID,
    			RESULT_FAIL,
    			true,
    			null,
    			null
    		},
    		{
    			TARGET_VALID,
    			RESULT_FAIL,
    			true,
    			new PluginPage(),
    			new PluginPage()
    		},
		});
	}
	
	private AppsController domain = new AppsController();
	private String target;
	private HttpServletRequest request;
	private String resultPath;
	private IPluginService pluginService;
	private PluginPage expectedPluginResults;
	private IServiceContracts contractService;
	private IServiceCallManager callManager;
	private boolean getPluginsCalled;
	
	public PlatformController_pluginAppGet_UnitTests(String target, String resultPath, boolean getPluginsCalled, PluginPage pluginResults, PluginPage expectedPluginResults)
	{
		this.target = target;
		this.resultPath = resultPath;
		this.expectedPluginResults = expectedPluginResults;
		this.getPluginsCalled = getPluginsCalled;
		
		//
		// Create session
		//
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn("Token");
		
		this.request = mock(HttpServletRequest.class);
		when(this.request.getSession()).thenReturn(session);
		when(this.request.getRequestURI()).thenReturn("/test");
		
		//
		// Create services
		//
		this.pluginService = mock(IPluginService.class);
		Mockito.when(this.pluginService.getPagePlugins(any(String.class), eq(target))).thenReturn(pluginResults);
		
		this.contractService = mock(IServiceContracts.class);
		this.callManager = mock(IServiceCallManager.class);
		
		this.domain.setPluginService(this.pluginService)
					.setContractService(this.contractService)
					.setCallManager(this.callManager);
	}
	
	@Test
	public void TestPluginApp() throws Exception
	{
		ModelAndView result = this.domain.pluginAppGet(this.target, this.request);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(this.resultPath, result.getViewName());
		
		if(this.expectedPluginResults != null)
		{
			Assert.assertArrayEquals(this.expectedPluginResults.getScripts(), (PluginScript[])result.getModel().get(PlatformController.MODEL_PLUGINS));
		}
		else
		{
			Assert.assertNull(result.getModel().get(PlatformController.MODEL_PLUGINS));
		}
		
		verify(this.pluginService, times(this.getPluginsCalled? 1 : 0)).getPagePlugins(any(String.class), eq(this.target));
	}
}
