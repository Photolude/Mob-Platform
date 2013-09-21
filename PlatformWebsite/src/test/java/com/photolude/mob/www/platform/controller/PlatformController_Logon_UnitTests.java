package com.photolude.mob.www.platform.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.photolude.mob.commons.service.clients.IPluginService;
import com.photolude.mob.commons.service.clients.IUserServiceClient;
import com.photolude.mob.www.platform.controller.PlatformController;
import com.photolude.mob.www.platform.model.LogonRequest;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import org.springframework.web.servlet.ModelAndView;

@RunWith(Parameterized.class)
public class PlatformController_Logon_UnitTests {
	private static final String REFERER = "http://referer.com";
	private static final String TOKEN_VALID = "This is a token";
	private static final String RESULT_VALID = "redirect:/";
	private static final String RESULT_REDIRECT = "redirect:" + REFERER + "?error=logon";
	private static final String EMAIL_VALID = "test@test.com";
	private static final String PASSWORD_VALID = "password";
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			// 0. Valid
    			EMAIL_VALID,
    			PASSWORD_VALID,
    			TOKEN_VALID,
    			1,
    			RESULT_VALID
    		},
    		{
    			// 1. Null email
    			null,
    			PASSWORD_VALID,
    			TOKEN_VALID,
    			0,
    			RESULT_REDIRECT
    		},
    		{
    			// 2. Empty email
    			"",
    			PASSWORD_VALID,
    			TOKEN_VALID,
    			0,
    			RESULT_REDIRECT
    		},
    		{
    			// 3. Null password
    			EMAIL_VALID,
    			null,
    			TOKEN_VALID,
    			0,
    			RESULT_REDIRECT
    		},
    		{
    			// 4. Empty password
    			EMAIL_VALID,
    			"",
    			TOKEN_VALID,
    			0,
    			RESULT_REDIRECT
    		},
    		{
    			// 5. Null token returned
    			EMAIL_VALID,
    			PASSWORD_VALID,
    			null,
    			1,
    			RESULT_REDIRECT
    		},
    		{
    			// 6. Empty token returned
    			EMAIL_VALID,
    			PASSWORD_VALID,
    			"",
    			1,
    			RESULT_REDIRECT
    		},
		});
	}
	
	private PlatformController domain = new PlatformController();
	private IUserServiceClient testClient;
	private IPluginService pluginService;
	private String email;
	private String password;
	private String expectedResult;
	private int expectedUserServiceCalls;
	
	public PlatformController_Logon_UnitTests(String email, String password, String clientToken, int expectedUserServiceCalls, String expectedResult)
	{
		this.testClient = mock(IUserServiceClient.class);
		this.pluginService = mock(IPluginService.class);
		
		Mockito.when(this.testClient.logon(email, password)).thenReturn(clientToken);
		
		this.email = email;
		this.password = password;
		this.expectedResult = expectedResult;
		this.expectedUserServiceCalls = expectedUserServiceCalls;
		
		this.domain.setUserServiceClient(this.testClient);
		this.domain.setPluginService(this.pluginService);
	}
	
	@Test
	public void ExecuteLogon() throws Exception
	{
		//
		// Construct logon data
		//
		LogonRequest logonData = new LogonRequest()
								.setEmail(this.email)
								.setPassword(this.password);
		
		//
		// Construct HttpRequest objects
		//
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(request.getHeader("referer")).thenReturn(REFERER);
		
		//
		// Make call being tested
		//
		ModelAndView outval = this.domain.logon(logonData, request);
		
		//
		// Validate results
		//
		Assert.assertNotNull(outval);
		String viewName = outval.getViewName();
		Assert.assertEquals(this.expectedResult, viewName);
		verify(this.testClient, times(this.expectedUserServiceCalls)).logon(this.email, this.password);
	}
}
