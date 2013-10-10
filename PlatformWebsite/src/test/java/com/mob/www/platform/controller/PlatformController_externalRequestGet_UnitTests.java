package com.mob.www.platform.controller;

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

import com.mob.www.platform.constants.TestConstants;
import com.mob.www.platform.controller.ExternalRequestController;
import com.mob.www.platform.controller.PlatformController;
import com.mob.www.platform.services.IServiceContracts;

@RunWith(Parameterized.class)
public class PlatformController_externalRequestGet_UnitTests {
	
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TestConstants.SERVICE_CALL_VALID,
    			true,
    			TestConstants.RESPONSE_BODY_VALID,
    			TestConstants.RESPONSE_BODY_VALID
    		},
    		{
    			TestConstants.SERVICE_CALL_VALID,
    			true,
    			null,
    			""
    		},
    		{
    			TestConstants.SERVICE_CALL_VALID,
    			true,
    			"",
    			""
    		},
    		{
    			TestConstants.SERVICE_CALL_VALID,
    			false,
    			TestConstants.RESPONSE_BODY_VALID,
    			""
    		},
    		{
    			null,
    			true,
    			TestConstants.RESPONSE_BODY_VALID,
    			TestConstants.RESPONSE_BODY_VALID
    		},
		});
	}
	
	private ExternalRequestController domain = new ExternalRequestController();
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
		
		this.domain.get(request, response);
		
		verify(this.contractService, times(this.isCallAllowed? 1 : 0)).callServiceWithGet(session, this.serviceCall);
	}
}
