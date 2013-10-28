package com.mob.www.platform.controller;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import com.mob.commons.service.clients.IUserServiceClient;
import com.mob.www.platform.controller.SessionListener;
import com.mob.www.platform.services.ServiceCallContext;

@RunWith(Parameterized.class)
public class SessionListener_UnitTests {
	private static final String TOKEN_VALID = "This is a token";
	
	@SuppressWarnings("rawtypes")
	@Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][] 
		{
    		{
    			TOKEN_VALID,
    			true
    		},
    		{
    			null,
    			false
    		},
    		{
    			"",
    			false
    		},
		});
	}
	
	private String userToken;
	private boolean logoutCalled;
	
	public SessionListener_UnitTests(String userToken, boolean logoutCalled)
	{
		this.userToken = userToken;
		this.logoutCalled = logoutCalled;
	}

	@Test
	public void TestEndSession()
	{
		IUserServiceClient userService = mock(IUserServiceClient.class);
		HttpSessionEvent eventObject = mock(HttpSessionEvent.class);
		HttpSession mockSession = mock(HttpSession.class);
		
		Mockito.when(eventObject.getSession()).thenReturn(mockSession);
		Mockito.when(mockSession.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn(this.userToken);
		
		SessionListener listener = new SessionListener();
		listener.setUserService(userService);
		listener.sessionDestroyed(eventObject);
		
		verify(userService, times(this.logoutCalled? 1 : 0)).logout(this.userToken);
	}
}
