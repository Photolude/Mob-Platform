package com.mob.www.platform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mob.www.platform.services.ServiceCallContext;
import com.mysql.jdbc.StringUtils;

public class SecurityInterceptor extends HandlerInterceptorAdapter {
	private String loggedOutPage = "";
	public String getLoggedOutPage(){ return this.loggedOutPage; }
	public SecurityInterceptor setLoggedOutPage(String value)
	{
		this.loggedOutPage = value;
		return this;
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
        if (request.getRequestURI().endsWith("/logon") || 
        		request.getRequestURI().endsWith("/logon/anonymous") ||
        		request.getRequestURI().endsWith("/logon/googleplus")) {
            return true;
        }
        
        boolean succeeded = false;
        ServiceCallContext context = null;
        
        try
        {
        	context = new ServiceCallContext(request);
        }
        catch(IllegalArgumentException e)
        {
        }
        
        if(context != null && !StringUtils.isNullOrEmpty(context.getUserToken()))
        {
        	succeeded = true;
        }
        
        if(!succeeded)
        {
        	response.sendRedirect(this.loggedOutPage);
        }
        return succeeded;
    }
}
