package com.mob.www.platform.controller;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mob.commons.service.clients.IUserServiceClient;
import com.mob.www.platform.services.ServiceCallContext;

public class SessionListener implements HttpSessionListener {
	private IUserServiceClient userService;
	public IUserServiceClient getUserService(){ return this.userService; }
	public SessionListener setUserService(IUserServiceClient value)
	{
		this.userService = value;
		return this;
	}

	public SessionListener()
	{
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEventObject) {
		HttpSession session = sessionEventObject.getSession();
		
		if(session != null)
		{
			if(this.userService == null)
			{
				ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
	        
		        if(ctx != null)
		        {
			        this.userService = (IUserServiceClient) ctx.getBean("userService");
		        }
			}

			String userToken = (String)session.getAttribute(ServiceCallContext.SESSION_USER_TOKEN);
			if(this.userService != null && userToken != null && userToken.length() > 0)
			{
				this.userService.logout(userToken);
			}
		}
	}

}
