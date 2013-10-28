package com.mob.www.platform.services;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.mob.commons.plugins.servicemodel.MainMenuItem;
import com.mob.commons.plugins.servicemodel.PluginDefinition;
import com.mob.commons.plugins.servicemodel.PluginScript;
import com.mysql.jdbc.StringUtils;

public class ServiceCallContext {
	public static final String SESSION_EXTERNALS = "externalservices";
	public static final String SESSION_USER_TOKEN = "userToken";
	public static final String SESSION_ROLE_MAP = "roleMap";
	public static final String SESSION_MENU = "menu";
	
	private static final String TOKEN_USER_TOKEN = "usertoken";
	public static final String ACTIVE_SCRIPTS = "activeScripts";
	
	private HttpSession session;
	private HttpServletRequest request;
	private String extendedPath;
	private Map<String, String> tokenLookup;
	
	public ServiceCallContext(HttpServletRequest request) throws IllegalArgumentException
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request provided is null");
		}
		
		this.request = request;
		this.session = request.getSession();
		if(this.session == null)
		{
			throw new IllegalArgumentException("The request provided does not have a session");
		}
	}
	
	public String getExtendedPath(){ return this.extendedPath; }
	public ServiceCallContext setExtendedPath(String value)
	{
		this.extendedPath = value;
		return this;
	}
	
	public Map<String, String> getTokenLookup()
	{
		if(this.tokenLookup == null)
		{
			this.tokenLookup = new HashMap<String,String>();
		
			String token = this.getUserToken();
			if(!StringUtils.isNullOrEmpty(token))
			{
				this.tokenLookup.put(TOKEN_USER_TOKEN, token);
			}
			
			Map<?, ?> map = request.getParameterMap();
			if(map != null)
			{
				for(Object name : map.keySet())
				{
					Object value = map.get(name);
					if(name != null && value != null)
					{
						tokenLookup.put(name.toString(), value.toString());
					}
				}
			}
		}
		return this.tokenLookup; 
	}
	public ServiceCallContext setTokenLookup(Map<String, String> value)
	{
		this.tokenLookup = value;
		return this;
	}
	
	public String getUserToken()
	{
		return (String) this.session.getAttribute(SESSION_USER_TOKEN);
	}
	public ServiceCallContext setUserToken(String token)
	{
		this.session.setAttribute(SESSION_USER_TOKEN, token);
		return this;
	}
	
	public MainMenuItem[] getMenuItems()
	{
		return (MainMenuItem[])session.getAttribute(SESSION_MENU);
	}
	public ServiceCallContext setMenuItems(MainMenuItem[] value)
	{
		this.session.setAttribute(SESSION_MENU, value);
		return this;
	}
	
	public ServiceCallContext setMenuData(String value)
	{
		session.setAttribute("menuData", value);
		return this;
	}
	
	
	public PluginScript[] getActiveScripts()
	{
		return (PluginScript[]) session.getAttribute(ACTIVE_SCRIPTS);
	}
	public ServiceCallContext setActiveScripts(PluginScript[] scripts)
	{
		this.session.setAttribute(ACTIVE_SCRIPTS, scripts);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getServiceLookup()
	{
		return (Map<String,String>)session.getAttribute(SESSION_EXTERNALS);
	}
	public ServiceCallContext setServiceLookup(Map<String,String> value)
	{
		this.session.setAttribute(SESSION_EXTERNALS, value);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,PluginDefinition> getRoleMap()
	{
		Map<String,PluginDefinition> retval = (Map<String,PluginDefinition>)this.session.getAttribute(SESSION_ROLE_MAP);
		
		if(retval == null)
		{
			synchronized(this.session)
			{
				retval = (Map<String,PluginDefinition>)this.session.getAttribute(SESSION_ROLE_MAP);
				if(retval == null)
				{
					retval = new HashMap<String,PluginDefinition>();
					this.session.setAttribute(SESSION_ROLE_MAP, retval);
				}
			}
		}
		
		return retval;
	}
}
