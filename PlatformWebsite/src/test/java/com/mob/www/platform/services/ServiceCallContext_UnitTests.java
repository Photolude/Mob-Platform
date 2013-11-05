package com.mob.www.platform.services;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import org.springframework.web.servlet.HandlerMapping;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginScript;

import static org.mockito.Mockito.*;

public class ServiceCallContext_UnitTests {
	
	private ServiceCallContext context;
	private HttpServletRequest request;
	private HttpSession session;
	
	public ServiceCallContext_UnitTests()
	{
		this.session = mock(HttpSession.class);
		
		this.request = mock(HttpServletRequest.class);
		when(request.getSession()).thenReturn(this.session);
	}
	
	@Before
	public void Setup()
	{
		
	}
	
	@Test
	public void ServiceCallContextTest_Valid()
	{
		when(this.session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn("token");
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertEquals("token", this.context.getUserToken()); 
	}
	
	@Test
	public void ServiceCallContextTest_Null()
	{
		when(this.session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn(null);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertEquals(null, this.context.getUserToken()); 
	}
	
	@Test
	public void ServiceCallContextTest_ActiveScripts_Valid()
	{
		PluginScript[] pluginScripts = new PluginScript[1];
		when(this.session.getAttribute(ServiceCallContext.ACTIVE_SCRIPTS)).thenReturn(pluginScripts);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertArrayEquals(pluginScripts, this.context.getActiveScripts()); 
	}
	
	@Test
	public void ServiceCallContextTest_ActiveScripts_Null()
	{
		PluginScript[] pluginScripts = null;
		when(this.session.getAttribute(ServiceCallContext.ACTIVE_SCRIPTS)).thenReturn(pluginScripts);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertArrayEquals(pluginScripts, this.context.getActiveScripts()); 
	}
	
	@Test
	public void ServiceCallContextTest_MenuItems_Valid()
	{
		MainMenuItem[] menu = new MainMenuItem[1];
		when(this.session.getAttribute(ServiceCallContext.SESSION_MENU)).thenReturn(menu);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertArrayEquals(menu, this.context.getMenuItems()); 
	}
	
	@Test
	public void ServiceCallContextTest_MenuItems_Null()
	{
		MainMenuItem[] menu = null;
		when(this.session.getAttribute(ServiceCallContext.SESSION_MENU)).thenReturn(menu);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertArrayEquals(menu, this.context.getMenuItems()); 
	}
	
	@Test
	public void ServiceCallContextTest_RoleMap_Valid()
	{
		Map<String,PluginDefinition> roleMap = new HashMap<String,PluginDefinition>();
		when(this.session.getAttribute(ServiceCallContext.SESSION_ROLE_MAP)).thenReturn(roleMap);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertEquals(roleMap, this.context.getRoleMap()); 
	}
	
	@Test
	public void ServiceCallContextTest_RoleMap_Null()
	{
		Map<String,PluginDefinition> roleMap = null;
		when(this.session.getAttribute(ServiceCallContext.SESSION_ROLE_MAP)).thenReturn(roleMap);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertNotNull(this.context.getRoleMap()); 
	}
	
	@Test
	public void ServiceCallContextTest_ServiceLookup_Valid()
	{
		Map<String,String> serviceLookup = new HashMap<String,String>();
		when(this.session.getAttribute(ServiceCallContext.SESSION_EXTERNALS)).thenReturn(serviceLookup);
		this.context = new ServiceCallContext(this.request);
		
		Assert.assertEquals(serviceLookup, this.context.getServiceLookup()); 
	}
	
	@Test
	public void ServiceCallContextTest_ServiceLookup_Null()
	{
		Map<String,String> serviceLookup = null;
		when(this.session.getAttribute(ServiceCallContext.SESSION_EXTERNALS)).thenReturn(serviceLookup);
		this.context = new ServiceCallContext(this.request);
		Assert.assertEquals(serviceLookup, this.context.getServiceLookup()); 
	}
	
	@Test
	public void ServiceCallContextTest_TokenLookup_UserToken()
	{
		when(this.session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn("token");
		this.context = new ServiceCallContext(this.request);
		Map<String,String> tokenLookup = this.context.getTokenLookup(); 
		
		Assert.assertEquals("token", tokenLookup.get("usertoken"));
	}
	
	@Test
	public void ServiceCallContextTest_TokenLookup_UserToken_Null()
	{
		when(this.session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN)).thenReturn(null);
		this.context = new ServiceCallContext(this.request);
		Map<String,String> tokenLookup = this.context.getTokenLookup(); 
		
		Assert.assertEquals(null, tokenLookup.get("usertoken"));
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_WithValue()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("test/test/1");
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals("1", result);
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_WithValueAndFrontSlash()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/test/test/1");
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals("1", result);
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_MultipleValues()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("test/test/1/2/3");
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals("1/2/3", result);
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_NoValue()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("test/test");
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_NullPath()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(null);
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void ServiceCallContextTest_ExtendedPath_IncompletePath()
	{
		when(this.request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("test");
		this.context = new ServiceCallContext(this.request);
		String result = this.context.getExtendedPath();
		
		Assert.assertEquals(null, result);
	}
}
