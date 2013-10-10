package com.mob.www.platform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.mob.www.platform.controller.PlatformController;
import com.mob.www.platform.controller.SecurityInterceptor;

import static org.mockito.Mockito.*;

public class SecurityInterceptor_UnitTests {
	private SecurityInterceptor interceptor = new SecurityInterceptor();
	
	HttpServletResponse response = mock(HttpServletResponse.class);
	
	public SecurityInterceptor_UnitTests()
	{
	}
	
	@Test
	public void Logon() throws Exception
	{
		
		boolean result = this.interceptor.preHandle(buildRequest("http://foo/logon", false, null), response, null);
		Assert.assertEquals(true, result);
	}
	
	@Test
	public void Index_NoSession() throws Exception
	{
		boolean result = this.interceptor.preHandle(buildRequest("http://foo/", false, null), response, null);
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void Index_NoToken() throws Exception
	{
		boolean result = this.interceptor.preHandle(buildRequest("http://foo/", true, null), response, null);
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void Index_ValidToken() throws Exception
	{
		boolean result = this.interceptor.preHandle(buildRequest("http://foo/", true, ""), response, null);
		Assert.assertEquals(true, result);
	}
	
	private static HttpServletRequest buildRequest(String uri, boolean hasSession, String token)
	{
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn(uri);
		
		if(hasSession)
		{
			HttpSession session = mock(HttpSession.class);
			Mockito.when(session.getAttribute(PlatformController.SESSION_USER_TOKEN)).thenReturn(token);
			
			Mockito.when(request.getSession(false)).thenReturn(session);
		}
		
		return request;
	}
}
