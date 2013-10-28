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

import org.springframework.web.servlet.HandlerMapping;

import static org.mockito.Mockito.*;

import com.mob.www.platform.constants.TestConstants;
import com.mob.www.platform.controller.ExternalRequestController;
import com.mob.www.platform.services.IServiceCallManager;
import com.mob.www.platform.services.IServiceContracts;
import com.mob.www.platform.services.ServiceCallContext;

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
	
	private ExternalRequestController controller = new ExternalRequestController();
	private IServiceContracts contractService = mock(IServiceContracts.class);
	private IServiceCallManager serviceManager = mock(IServiceCallManager.class);
	private String serviceCall;
	
	public PlatformController_externalRequestGet_UnitTests(String serviceCall, boolean isCallAllowed, String serviceResponse, String expectedBody)
	{
		this.serviceCall = serviceCall;
		
		when(this.contractService.isCallAllowed(eq(serviceCall), any(ServiceCallContext.class))).thenReturn(isCallAllowed);
		
		controller.setServiceCallManager(this.serviceManager);
	}
	
	@Test
	public void externalRequestGetTest() throws Exception
	{
		HttpSession session = mock(HttpSession.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(session);
		when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(this.serviceCall);
		
		HttpServletResponse response = mock(HttpServletResponse.class);
		
		this.controller.get(request, response);
	}
}
