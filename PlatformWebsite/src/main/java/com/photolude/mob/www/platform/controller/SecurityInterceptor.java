package com.photolude.mob.www.platform.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
        if (request.getRequestURI().endsWith("/logon")) {
            return true;
        }
        
        boolean succeeded = false;
        HttpSession session = request.getSession(false);
        
        if(session != null && session.getAttribute(PlatformController.SESSION_USER_TOKEN) != null)
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
