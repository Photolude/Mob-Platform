package com.photolude.mob.www.platform.controller;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.photolude.mob.www.platform.services.IUserService;

public class SessionListener implements HttpSessionListener {
	private IUserService userService;
	public IUserService getUserService(){ return this.userService; }
	public SessionListener setUserService(IUserService value)
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
			        this.userService = (IUserService) ctx.getBean("userService");
		        }
			}
		
			String userToken = (String)session.getAttribute(PlatformController.SESSION_USER_TOKEN);
			if(this.userService != null && userToken != null && userToken.length() > 0)
			{
				this.userService.logout(userToken);
			}
		}
	}

}
