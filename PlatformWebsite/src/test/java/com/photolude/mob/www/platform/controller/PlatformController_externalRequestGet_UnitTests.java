package com.photolude.mob.www.platform.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
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
	
	public PlatformController_externalRequestGet_UnitTests(String serviceCall, boolean isCallAllowed, String serviceResponse, String expectedBody)
	{
		this.serviceCall = serviceCall;
		this.isCallAllowed = isCallAllowed;
		
		Mockito.when(this.contractService.isCallAllowed(any(HttpSession.class), eq(serviceCall))).thenReturn(isCallAllowed);
		domain.setContractService(this.contractService);
	}
	
	@Test
	public void externalRequestGetTest() throws Exception
	{
		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(request.getParameter(PlatformController.API_REQUEST_PARAM)).thenReturn(this.serviceCall);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		this.domain.externalRequestGet(request, response);
		
		verify(this.contractService, times(this.isCallAllowed? 1 : 0)).callServiceWithGet(session, this.serviceCall);
	}
}
