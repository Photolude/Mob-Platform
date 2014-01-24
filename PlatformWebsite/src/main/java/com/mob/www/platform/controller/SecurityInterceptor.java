package com.mob.www.platform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mob.www.platform.services.IAnonymousAccess;
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
	
	private IAnonymousAccess anonymousAccess;
	public IAnonymousAccess getAnonymousAccess(){ return this.anonymousAccess; }
	public SecurityInterceptor setAnonymousAccess(IAnonymousAccess value)
	{
		this.anonymousAccess = value;
		return this;
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
        if (request.getRequestURI().endsWith("/logon") || 
        		request.getRequestURI().endsWith("/logon/anonymous") ||
        		request.getRequestURI().endsWith("/logon/googleplus") ||
        		request.getRequestURI().endsWith("/schema/ppl_1_0.xsd")) {
            return true;
        }
        
        ServiceCallContext context = ServiceCallContext.getContext(request);
        
        if(context != null && !StringUtils.isNullOrEmpty(context.getUserToken()))
        {
        	return true;
        }
        
        if(this.anonymousAccess != null && !StringUtils.isNullOrEmpty(this.anonymousAccess.getDefaultIdentity()))
        {
        	response.sendRedirect("/logon/anonymous");
        	return false;
        }
        
    	response.sendRedirect(this.loggedOutPage);
    	return false;
    }
}
