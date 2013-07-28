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

import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import static org.mockito.Mockito.*;

import com.photolude.mob.www.platform.controller.PlatformController;
import com.photolude.mob.www.platform.services.IServiceContracts;

@RunWith(Parameterized.class)
public class PlatformController_externalRequestGet_UnitTests {
	public static final String SERVICE_CALL_VALID = "http://serviceCall/";
	public static final String RESPONSE_BODY_VALID = "This is the response";
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			SERVICE_CALL_VALID,
    			true,
    			RESPONSE_BODY_VALID,
    			RESPONSE_BODY_VALID
    		},
    		{
    			SERVICE_CALL_VALID,
    			true,
    			null,
    			""
    		},
    		{
    			SERVICE_CALL_VALID,
    			true,
    			"",
    			""
    		},
    		{
    			SERVICE_CALL_VALID,
    			false,
    			RESPONSE_BODY_VALID,
    			""
    		},
    		{
    			null,
    			true,
    			RESPONSE_BODY_VALID,
    			RESPONSE_BODY_VALID
    		},
		});
	}
	
	private PlatformController domain = new PlatformController();
	private IServiceContracts contractService = mock(IServiceContracts.class);
	private boolean isCallAllowed; 
	private String serviceCall;
	private String expectedBody;
	
	public PlatformController_externalRequestGet_UnitTests(String serviceCall, boolean isCallAllowed, String serviceResponse, String expectedBody)
	{
		this.serviceCall = serviceCall;
		this.isCallAllowed = isCallAllowed;
		this.expectedBody = expectedBody;
		
		Mockito.when(this.contractService.isCallAllowed(any(HttpSession.class), eq(serviceCall))).thenReturn(isCallAllowed);
		Mockito.when(this.contractService.callServiceWithGet(this.serviceCall)).thenReturn(serviceResponse);
		domain.setContractService(this.contractService);
	}
	
	@Test
	public void externalRequestGetTest() throws Exception
	{
		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(request.getParameter(PlatformController.API_REQUEST_PARAM)).thenReturn(this.serviceCall);
		
		ModelAndView response = this.domain.externalRequestGet(request);
		
		Assert.assertNotNull(response);
		Assert.assertEquals("apiResponse", response.getViewName());
		Assert.assertEquals(this.expectedBody, (String)response.getModel().get(PlatformController.MODEL_API_BODY));
		
		verify(this.contractService, times(this.isCallAllowed? 1 : 0)).callServiceWithGet(this.serviceCall);
	}
}
